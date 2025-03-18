/*-
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2025 The Ontopia Project
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

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import net.ontopia.utils.DurationConverter;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A datasource wrapping the DBCP2 connection pooling. Uses bean-utils to populate the settings,
 * see the specific config documentation for all possible options.

 * @since %NEXT%
 * @see AbandonedConfig
 * @see GenericObjectPoolConfig
 */
public class DBCPDataSource extends PoolingDataSource<PoolableConnection> implements InstrumentedDataSourceIF {
  private static final Logger logger = LoggerFactory.getLogger(DBCPDataSource.class);

  public static final String RDBMS_DRIVER = RDBMSStorage.PROPERTIES_ROOT + "DriverClass";
  public static final String RDBMS_CONNECTION_STRING = RDBMSStorage.PROPERTIES_ROOT + "ConnectionString";
  public static final String RDBMS_USERNAME = RDBMSStorage.PROPERTIES_ROOT + "UserName";
  public static final String RDBMS_PASSWORD = RDBMSStorage.PROPERTIES_ROOT + "Password";
  public static final String VALIDATION_QUERY = RDBMSStorage.PROPERTIES_ROOT + "ValidationQuery";

  public static final String POOL = RDBMSStorage.PROPERTIES_ROOT + "ConnectionPool.";
  public static final String ABANDON = RDBMSStorage.PROPERTIES_ROOT + "Abandoned.";
  public static final String VALIDATION_QUERY_TIMEOUT = POOL + "ValidationQueryTimeout"; // in seconds

  // defaults
  public static final int DEFAULT_VALIDATION_TIMEOUT = 10; // 10s
  public static final String DEFAULT_VALIDATION_QUERY = "select seq_count from TM_ADMIN_SEQUENCE where seq_name = '<GLOBAL>'";
  public static final int DEFAULT_USER_TIMEOUT = 10000; // 10s
  public static final int DEFAULT_MIN_IDLE = 0;
  public static final int DEFAULT_MAX_IDLE = 20;
  public static final int DEFAULT_MAX_ACTIVE = 50;
  public static final int DEFAULT_IDLE_TIMEOUT = 5 * 60 * 1000; // 5m

  public static final int DEFAULT_ABANDON_TIMEOUT = 600; // 10m

  private DBCPDataSource(ObjectPool<PoolableConnection> pool) {
    super(pool);
  }

  public static DBCPDataSource fromConfiguration(Map<String, String> properties) {
    // tweak beanutils
    BeanUtilsBean.setInstance(new BeanUtilsBean(new DurationConverter.DurationAwareConvertUtilsBean()));

    // Make sure driver is registered
    loadDriver(PropertyUtils.getProperty(properties, RDBMS_DRIVER));

    ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
            PropertyUtils.getProperty(properties, RDBMS_CONNECTION_STRING),
            PropertyUtils.getProperty(properties, RDBMS_USERNAME),
            PropertyUtils.getProperty(properties, RDBMS_PASSWORD));

    PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);

    // default config
    poolableConnectionFactory.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    poolableConnectionFactory.setDefaultAutoCommit(false);

    // apply config
    poolableConnectionFactory.setValidationQueryTimeout(Duration.ofSeconds(PropertyUtils.getInt(VALIDATION_QUERY_TIMEOUT, DEFAULT_VALIDATION_TIMEOUT)));
    poolableConnectionFactory.setValidationQuery(PropertyUtils.getProperty(properties, VALIDATION_QUERY, DEFAULT_VALIDATION_QUERY));

    ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(
            poolableConnectionFactory,
            getGenericObjectPoolConfig(properties),
            getAbandonedConfig(properties));

    poolableConnectionFactory.setPool(connectionPool);

    if (logger.isTraceEnabled()) {
      logger.trace("Connection pool setup as {}", connectionPool);
    }

    return new DBCPDataSource(connectionPool);
  }

  private static void loadDriver(String driver) {
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class<?> driverClass = Class.forName(driver, true, classLoader);
      logger.debug("Found driver class {}", driverClass.getName());
    } catch (ClassNotFoundException Cnfe) {
      throw new OntopiaRuntimeException("Couldn't find JDBC driver class '" + driver + "' (name taken from property " + RDBMS_DRIVER + ")");
    }
  }

  private static GenericObjectPoolConfig<PoolableConnection> getGenericObjectPoolConfig(Map<String, String> properties) {
    GenericObjectPoolConfig<PoolableConnection> config = new GenericObjectPoolConfig<>();

    // defaults
    config.setBlockWhenExhausted(true); // dbcp = true
    config.setMaxWait(Duration.ofMillis(DEFAULT_USER_TIMEOUT)); // dbcp = -1
    config.setMinIdle(DEFAULT_MIN_IDLE); // dbcp = 0
    config.setMaxIdle(DEFAULT_MAX_IDLE); // dbcp = 8
    config.setMaxTotal(DEFAULT_MAX_ACTIVE);  // dbcp = 8

    config.setTestOnBorrow(true); // dbcp = false
    config.setTimeBetweenEvictionRuns(Duration.ofSeconds(DEFAULT_IDLE_TIMEOUT)); // dbcp = -1
    config.setMinEvictableIdleDuration(Duration.ofSeconds(DEFAULT_IDLE_TIMEOUT)); // dbcp = 30m
    config.setSoftMinEvictableIdleDuration(Duration.ofSeconds(DEFAULT_IDLE_TIMEOUT)); // dbcp = -1 // POINTLESS

    return applyConfig(config, properties, POOL);
  }

  private static AbandonedConfig getAbandonedConfig(Map<String, String> properties) {
    AbandonedConfig config = new AbandonedConfig();

    // defaults
    config.setRemoveAbandonedTimeout(Duration.ofSeconds(DEFAULT_ABANDON_TIMEOUT)); // dbcp = 5m
    config.setLogAbandoned(true); // dbcp = unset
    config.setLogWriter(new PrintWriter(new AbandonedConnectionLogger(), true));

    return applyConfig(config, properties, ABANDON);
  }

  private static <T> T applyConfig(T bean, Map<String, String> properties, String prefix) {

    BeanUtilsBean instance = BeanUtilsBean.getInstance();

    Map<String, String> subset = PropertyUtils.subset(properties, prefix);
    try {
      Set<String> knownProperties = BeanUtils.describe(bean).keySet();
      logger.trace("Known properties of {}: {}", bean.getClass().getName(), knownProperties);
      for (String property : subset.keySet()) {
        PropertyDescriptor propertyDescriptor = instance.getPropertyUtils().getPropertyDescriptor(bean, property);


        logger.info("{}", propertyDescriptor);

        if ((propertyDescriptor == null) || (propertyDescriptor.getWriteMethod() == null)) {
          throw new OntopiaRuntimeException("Could not configure OntopiaDataSource: unknown configuration item " + prefix + property);
        }
      }
      instance.populate(bean, subset);
      return bean;
    } catch (IllegalAccessException iae) {
      if (iae.getCause() instanceof NoSuchMethodError) {
        throw new OntopiaRuntimeException("Could not configure OntopiaDataSource: unknown configuration item " + iae.getCause().getMessage());
      }
      throw new OntopiaRuntimeException("Could not configure OntopiaDataSource: " + iae.getMessage(), iae);
    } catch (NoSuchMethodException nsme) {
      throw new OntopiaRuntimeException("Could not configure OntopiaDataSource: unknown configuration item " + nsme.getMessage());
    } catch (InvocationTargetException ite) {
      throw new OntopiaRuntimeException("Could not configure OntopiaDataSource: " + ite.getMessage(), ite);
    }
  }

  private static class AbandonedConnectionLogger extends Writer {

    private StringBuilder buffer = new StringBuilder();

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
      buffer.append(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
      logger.warn(StringUtils.removeEnd(buffer.toString(), "\n"));
      buffer = new StringBuilder();
    }

    @Override
    public void close() throws IOException {
      // no-op
    }
  }

  // -- METRICS

  @SuppressWarnings("unchecked")
  private GenericObjectPool<PoolableConnection> getGenericObjectPool() {
    return (GenericObjectPool) getPool();
  }

  @Override
  public long getConnectionsClosed() {
    return getGenericObjectPool().getDestroyedCount();
  }

  @Override
  public long getConnectionsOpened() {
    return getGenericObjectPool().getCreatedCount();
  }

  public int getNumActive() {
    return getGenericObjectPool().getNumActive();
  }

  public int getNumIdle() {
    return getGenericObjectPool().getNumIdle();
  }

  public int getMaxTotal() {
    return getGenericObjectPool().getMaxTotal();
  }

  public int getMinIdle() {
    return getGenericObjectPool().getMinIdle();
  }

  public int getMaxIdle() {
    return getGenericObjectPool().getMaxIdle();
  }

  public long getConnectionsBorrowed() {
    return getGenericObjectPool().getBorrowedCount();
  }

  public long getConnectionsReturned() {
    return getGenericObjectPool().getReturnedCount();
  }

  public long getConnectionsClosedByValidation() {
    return getGenericObjectPool().getDestroyedByBorrowValidationCount();
  }

  public long getConnectionsClosedByEviction() {
    return getGenericObjectPool().getDestroyedByEvictorCount();
  }
}
