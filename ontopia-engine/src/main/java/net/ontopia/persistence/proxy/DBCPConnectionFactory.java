/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.persistence.proxy;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.dbcp.AbandonedConfig;
import org.apache.commons.dbcp.AbandonedObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/** 
 * INTERNAL: Apache Commons DBCP connection factory implementation.
 */

public class DBCPConnectionFactory extends AbstractConnectionFactory {

  public static final String EXHAUSED_BLOCK = "block";
  public static final String EXHAUSED_GROW = "grow";
  public static final String EXHAUSED_FAIL = "fail";

  // configuration and defaul values
  public static final String ROOT = "net.ontopia.topicmaps.impl.rdbms.ConnectionPool.";

  public static final String MIN_IDLE = ROOT + "MinimumSize";
  public static final int DEFAULT_MIN_IDLE = GenericObjectPool.DEFAULT_MIN_IDLE; // 0

  public static final String MAX_ACTIVE = ROOT + "MaximumSize";
  public static final int DEFAULT_MAX_ACTIVE = 50;

  public static final String MAX_IDLE = ROOT + "MaximumIdle";
  public static final int DEFAULT_MAX_IDLE = 20;

  public static final String USER_TIMEOUT = ROOT + "UserTimeout";
  public static final int DEFAULT_USER_TIMEOUT = 10000; // 10s

  public static final String IDLE_TIMEOUT = ROOT + "IdleTimeout";
  public static final int DEFAULT_IDLE_TIMEOUT = 5 * 60 * 1000; // 5m

  public static final String SOFT_MAXIMUM = ROOT + "SoftMaximum";
  public static final boolean DEFAULT_SOFT_MAXIMUM = false;

  public static final String EXHAUSTED_ACTION = ROOT + "WhenExhaustedAction";
  public static final String POOL_STATEMENTS = ROOT + "PoolStatements";
  public static final String VALIDATION_QUERY = ROOT + "ValidationQuery";

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(DBCPConnectionFactory.class.getName());

  protected AbandonedObjectPool pool;
  protected DataSource datasource;  
  protected TraceablePoolableConnectionFactory pcfactory;
  protected boolean readOnly;
  protected int defaultTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
  
  public DBCPConnectionFactory(Map<String, String> properties, boolean readOnly) {
    super(properties);
    this.readOnly = readOnly;
    // set up connection pool
    initPool();
  }
  
  protected void initPool() {
    // Read/Write by default
    log.debug("Creating new DBCP connection factory, readonly=" + readOnly);

    // Set up connection pool
    AbandonedConfig config = new AbandonedConfig();
    if (timeout > 0) {
      config.setRemoveAbandoned(true);
      config.setRemoveAbandonedTimeout(timeout);
      config.setLogAbandoned(true);
      config.setLogWriter(new PrintWriter(new TraceLogger(), true));
    }
    pool = new AbandonedObjectPool(null, config);

    pool.setMinIdle(getIntProperty(MIN_IDLE, DEFAULT_MIN_IDLE));
    pool.setMaxActive(getIntProperty(MAX_ACTIVE, DEFAULT_MAX_ACTIVE)); // -1 = no limit
    pool.setMaxIdle(getIntProperty(MAX_IDLE, DEFAULT_MAX_IDLE)); // -1 = no limit
    pool.setMaxWait(getIntProperty(USER_TIMEOUT, DEFAULT_USER_TIMEOUT)); // -1 = never
    int etime = getIntProperty(IDLE_TIMEOUT, DEFAULT_IDLE_TIMEOUT); // -1 = never
    pool.setTimeBetweenEvictionRunsMillis(etime);
    pool.setSoftMinEvictableIdleTimeMillis(etime);
    boolean softmax = MapUtils.getBoolean(properties, SOFT_MAXIMUM, DEFAULT_SOFT_MAXIMUM);
    pool.setWhenExhaustedAction(softmax ? GenericObjectPool.WHEN_EXHAUSTED_GROW : GenericObjectPool.WHEN_EXHAUSTED_BLOCK);

    // allow the user to overwrite exhausted options
    // warning: when set to fail, make sure Maximum and Minimum are set correctly
    // warning: when set to block, make sure a propper usertimeout is set, or pool will block forever
    String action = PropertyUtils.getProperty(properties, EXHAUSTED_ACTION, false);
    if (action != null) {
      switch (action) {
        case EXHAUSED_BLOCK: pool.setWhenExhaustedAction(GenericKeyedObjectPool.WHEN_EXHAUSTED_BLOCK); break;
        case EXHAUSED_GROW: pool.setWhenExhaustedAction(GenericKeyedObjectPool.WHEN_EXHAUSTED_GROW); break;
        case EXHAUSED_FAIL: pool.setWhenExhaustedAction(GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL); break;
        default: break;
      }
    }

    // Test on borrow
    pool.setTestOnBorrow(true);

    log.debug("DBCP connection {}-pool configured:", readOnly ? "ro" : "rw");
    log.debug("  minIdle = {}", pool.getMinIdle());
    log.debug("  maxIdle = {}", pool.getMaxIdle());
    log.debug("  maxActive = {}", pool.getMaxActive());
    log.debug("  maxWait = {}", pool.getMaxWait());
    log.debug("  evictionTime = {}", pool.getTimeBetweenEvictionRunsMillis());
    switch (pool.getWhenExhaustedAction()) {
      case GenericKeyedObjectPool.WHEN_EXHAUSTED_BLOCK: log.debug("  exhaustedAction = BLOCK"); break;
      case GenericKeyedObjectPool.WHEN_EXHAUSTED_GROW: log.debug("  exhaustedAction = GROW"); break;
      case GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL: log.debug("  exhaustedAction = FAIL"); break;
      default: break;
    }
    if (config.getRemoveAbandoned()) {
      log.debug("  removeAbandoned = true");
      log.debug("  removeAbandonedTimeout = {}", config.getRemoveAbandonedTimeout());
    } else {
      log.debug("  removeAbandoned = false");
    }

    // Statement pool
    GenericKeyedObjectPoolFactory stmpool = null;
    if (MapUtils.getBoolean(properties, POOL_STATEMENTS, true)) {
      log.debug("Using prepared statement pool: Yes");
      stmpool = new GenericKeyedObjectPoolFactory(null, 
                                                  -1, // unlimited maxActive (per key)
                                                  GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL, 
                                                  0, // maxWait
                                                  1, // maxIdle (per key) 
                                                  GenericKeyedObjectPool.DEFAULT_MAX_TOTAL); 
    } else {
      log.debug("Using prepared statement pool: No");
    }

    // Get validation query
    String vquery = PropertyUtils.getProperty(properties, VALIDATION_QUERY, false);
    if (vquery == null)
      vquery = "select seq_count from TM_ADMIN_SEQUENCE where seq_name = '<GLOBAL>'";

    try {
      // Make sure driver is registered
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class.forName(getDriver(), true, classLoader);
      // Create connection factory
      ConnectionFactory cfactory;
      if (getUserName() == null || getPassword() == null) {
        Properties props = new Properties();
        props.putAll(properties);        
        cfactory = new DriverManagerConnectionFactory(getConnectionString(), props);
      } else {
        cfactory = new DriverManagerConnectionFactory(getConnectionString(), getUserName(), getPassword());
    }

      // Create data source
      this.pcfactory =
        new TraceablePoolableConnectionFactory(cfactory, pool, stmpool, vquery, readOnly, config);
    
      // Set default transaction isolation level
      pcfactory.setDefaultTransactionIsolation(defaultTransactionIsolation);

      this.datasource = new PoolingDataSource(pool);
    } catch (Exception e) {
      throw new OntopiaRuntimeException("Problems occurred when setting up DBCP connection pool.", e);
    }
  }

  private int getIntProperty(String key, int defaultValue) {
    return PropertyUtils.getInt(PropertyUtils.getProperty(properties, key, false), defaultValue);
  }

    @Override
  public Connection requestConnection() throws SQLException {
    log.debug("Requesting connection from {}-pool.", readOnly ? "ro" : "rw");
    return datasource.getConnection();
  }

    @Override
  public void close() {
    // Release generic pool
    try {
      pool.close();
    } catch (Exception e) {
      throw new OntopiaRuntimeException("Problems occurred when closing DBCP connection pool.", e);
    }
  }

  public void writeReport(java.io.Writer out) throws java.io.IOException {
    final String BR = "<br>\n";
    out.write("Active connections: " + pool.getNumActive() + " (max: " + pool.getMaxActive() + ")<br>\n");
    out.write("Idle connections: " + pool.getNumIdle() + " (min: " + pool.getMinIdle() + " max: " + pool.getMaxIdle() + ")<br>\n");
    out.write("Connections created: " + pcfactory.objectsCreated + BR);
    out.write("Connections destroyed: " + pcfactory.objectsDestroyed + BR);
    out.write("Connections validated: " + pcfactory.objectsValidated + BR);
    out.write("Connections activated: " + pcfactory.objectsActivated + BR);
    out.write("Connections passivated: " + pcfactory.objectsPassivated + BR);
  }

  static private class TraceablePoolableConnectionFactory extends PoolableConnectionFactory {

    private int objectsCreated;
    private int objectsDestroyed;
    private int objectsValidated;
    private int objectsActivated;
    private int objectsPassivated;

    TraceablePoolableConnectionFactory(ConnectionFactory connFactory, ObjectPool pool, KeyedObjectPoolFactory stmtPoolFactory, String validationQuery, boolean defaultReadOnly, AbandonedConfig config) {
      super(connFactory, pool, stmtPoolFactory, validationQuery, defaultReadOnly, false, config); // Auto-commit disabled by default
    }

    // PoolableObjectFactory implementation

    @Override
    public Object makeObject() throws Exception {
      Object o = super.makeObject();
      objectsCreated++;
      return o;
    }

    @Override
    public void destroyObject(Object obj) throws Exception {
      super.destroyObject(obj);
      objectsDestroyed++;
    }

    @Override
    public boolean validateObject(Object obj) {
      boolean result = super.validateObject(obj);
      objectsValidated++;
      return result;
    }

    @Override
    public void activateObject(Object obj) throws Exception {
      if (log.isDebugEnabled()) { log.debug("Got connection {} from {}-pool", Integer.toHexString(obj.hashCode()), _defaultReadOnly ? "ro" : "rw"); }
      super.activateObject(obj);
      objectsActivated++;
    }

    @Override
    public void passivateObject(Object obj) throws Exception {
      if (log.isDebugEnabled()) { log.debug("Returned connection {} to pool", Integer.toHexString(obj.hashCode()),  _defaultReadOnly ? "ro" : "rw"); }
      super.passivateObject(obj);
      objectsPassivated++;
    }

  }

  private class TraceLogger extends Writer {

    private StringBuffer buffer = new StringBuffer();

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
      buffer.append(cbuf, off, len);
    }

    @Override public void flush() throws IOException {
      log.warn(StringUtils.removeEnd(buffer.toString(), "\n"));
      buffer = new StringBuffer();
    }

    @Override public void close() throws IOException {
      // no-op
    }
  }
}





