// $Id: DBCPConnectionFactory.java,v 1.25 2007/06/12 12:56:05 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/** 
 * INTERNAL: Jakarta DBCP connection factory implementation.
 */

public class DBCPConnectionFactory extends AbstractConnectionFactory {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(DBCPConnectionFactory.class.getName());

  protected GenericObjectPool pool;
  protected DataSource datasource;  
  protected TraceablePoolableConnectionFactory pcfactory;
  protected boolean defaultReadOnly;
  protected int defaultTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
  
  public DBCPConnectionFactory(Map properties, boolean defaultReadOnly) {
    super(properties);
    this.defaultReadOnly = defaultReadOnly;
    // set up connection pool
    initPool();
  }
  
  protected void initPool() {
    // Set up connection pool
    pool = new GenericObjectPool(null);

    // Read/Write by default
    boolean readonly = defaultReadOnly;
    // Auto-commit disabled by default
    boolean autocommit = readonly;
    log.debug("Creating new DBCP connection factory, readonly=" + readonly + ", autocommit=" + autocommit);

    // Set minimum pool size (default: 20)
    String _minsize = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.ConnectionPool.MinimumSize", false);
    int minsize = (_minsize == null ? 20 : Integer.parseInt(_minsize));
    log.debug("Setting ConnectionPool.MinimumSize '" + minsize + "'");
    pool.setMaxIdle(minsize); // 0 = no limit
    
    // Set maximum pool size (default: Integer.MAX_VALUE)
    String _maxsize = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.ConnectionPool.MaximumSize", false);
    int maxsize = (_maxsize == null ? 0 : Integer.parseInt(_maxsize));
    log.debug("Setting ConnectionPool.MaximumSize '" + maxsize + "'");
    pool.setMaxActive(maxsize); // 0 = no limit

    // Set user timeout (default: never)
    String _utimeout = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.ConnectionPool.UserTimeout", false);
    int utimeout = (_utimeout == null ? -1 : Integer.parseInt(_utimeout));
    pool.setMaxWait(utimeout); // -1 = never
    
    // Set soft maximum - emergency objects (default: true)
    boolean softmax = PropertyUtils.isTrue(properties, "net.ontopia.topicmaps.impl.rdbms.ConnectionPool.SoftMaximum", true);
    log.debug("Setting ConnectionPool.SoftMaximum '" + softmax + "'");
    if (softmax)
      pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);
    else
      pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
    
    // Statement pool
    GenericKeyedObjectPoolFactory stmpool = null;
    if (PropertyUtils.isTrue(properties, "net.ontopia.topicmaps.impl.rdbms.ConnectionPool.PoolStatements", true)) {
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

    // Test on borrow
    pool.setTestOnBorrow(true);

    // Get validation query
    String vquery = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.ConnectionPool.ValidationQuery", false);
    if (vquery == null)
      vquery = "select seq_count from TM_ADMIN_SEQUENCE where seq_name = '<GLOBAL>'";

    try {
      // Make sure driver is registered
      Class.forName(getDriver());
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
        new TraceablePoolableConnectionFactory(cfactory, pool, stmpool, vquery, readonly, autocommit);
    
      // Set default transaction isolation level
      pcfactory.setDefaultTransactionIsolation(defaultTransactionIsolation);

      this.datasource = new PoolingDataSource(pool);
    } catch (Exception e) {
      throw new OntopiaRuntimeException("Problems occurred when setting up DBCP connection pool.", e);
    }
  }

  public Connection requestConnection() throws SQLException {
    log.debug("Requesting connection from dbcp pool.");
    return datasource.getConnection();
  }

  public void close() {
    // Release generic pool
    try {
      pool.close();
    } catch (Exception e) {
      throw new OntopiaRuntimeException("Problems occurred when closing DBCP connection pool.", e);
    }
  }

  public void writeReport(java.io.Writer out) throws java.io.IOException {
    out.write("Active connections: " + pool.getNumActive() + " (max: " + pool.getMaxActive() + ")<br>\n");
    out.write("Idle connections: " + pool.getNumIdle() + " (min: " + pool.getMinIdle() + " max: " + pool.getMaxIdle() + ")<br>\n");
    out.write("Connections created: " + pcfactory.objectsCreated + "<br>\n");
    out.write("Connections destroyed: " + pcfactory.objectsDestroyed + "<br>\n");
    out.write("Connections validated: " + pcfactory.objectsValidated + "<br>\n");
    out.write("Connections activated: " + pcfactory.objectsActivated + "<br>\n");
    out.write("Connections passivated: " + pcfactory.objectsPassivated + "<br>\n");
  }

  static private class TraceablePoolableConnectionFactory extends PoolableConnectionFactory {

    private int objectsCreated;
    private int objectsDestroyed;
    private int objectsValidated;
    private int objectsActivated;
    private int objectsPassivated;

    TraceablePoolableConnectionFactory(ConnectionFactory connFactory, ObjectPool pool, KeyedObjectPoolFactory stmtPoolFactory, String validationQuery, boolean defaultReadOnly, boolean defaultAutoCommit) {
      super(connFactory, pool, stmtPoolFactory, validationQuery, defaultReadOnly, defaultAutoCommit);
    }

    // PoolableObjectFactory implementation

    public Object makeObject() throws Exception {
      Object o = super.makeObject();
      objectsCreated++;
      return o;
    }

    public void destroyObject(Object obj) throws Exception {
      super.destroyObject(obj);
      objectsDestroyed++;
    }

    public boolean validateObject(Object obj) {
      boolean result = super.validateObject(obj);
      objectsValidated++;
      return result;
    }

    public void activateObject(Object obj) throws Exception {
      super.activateObject(obj);
      objectsActivated++;
    }

    public void passivateObject(Object obj) throws Exception {
      super.passivateObject(obj);
      objectsPassivated++;
    }

  }
  
}





