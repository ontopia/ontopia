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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import javax.sql.DataSource;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A datasource creating connections from a {@link DriverManagerConnectionFactory}.
 * @since %NEXT%
 * @see AbandonedConfig
 * @see GenericObjectPoolConfig
 */
public class DriverDataSource implements DataSource, InstrumentedDataSourceIF {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DriverDataSource.class);

  private final DriverManagerConnectionFactory connectionFactory;
  private AtomicLong openedCounter = new AtomicLong();
  private AtomicLong forceClosedCounter = new AtomicLong();
  protected int timeout;
  protected int loginTimeout;
  protected ScheduledExecutorService connectionTimeoutExecutor = null;

  public DriverDataSource(Map<String, String> properties) {
    String driver = PropertyUtils.getProperty(properties, RDBMSStorage.PROPERTIES_ROOT + "DriverClass");
    try {
      // load driver class
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class.forName(driver, true, classLoader);
    } catch (ClassNotFoundException e) {
      throw new OntopiaRuntimeException("Couldn't find JDBC driver class '" + driver + "' (name taken from init property net.ontopia.topicmaps.impl.rdbms.DriverClass)");
    }

    connectionFactory = new DriverManagerConnectionFactory(
            PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.ConnectionString"),
            properties.get(RDBMSStorage.PROPERTIES_ROOT + "UserName"),
            properties.get(RDBMSStorage.PROPERTIES_ROOT + "Password"));

    loginTimeout = PropertyUtils.getInt(properties.get(RDBMSStorage.PROPERTIES_ROOT + "connection.LoginTimout"), -1);
    timeout = PropertyUtils.getInt(properties.get(RDBMSStorage.PROPERTIES_ROOT + "connection.AbandonedConnectionTimeout"), 600); // 10min
    if (timeout > -1) {
      connectionTimeoutExecutor = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder().namingPattern("ontopia-connectionCleanup-").daemon(true).build());
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    Connection connection = connectionFactory.createConnection();
    openedCounter.incrementAndGet();
    return connection;
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return getConnection();
  }

  @Override
  public long getConnectionsClosed() {
    return forceClosedCounter.get();
  }

  @Override
  public long getConnectionsOpened() {
    return openedCounter.get();
  }

  protected Connection applyTimeout(Connection connection) {
    if (timeout > -1) {
      final Exception trace = new Exception();
      connectionTimeoutExecutor.schedule(() -> {
        try {
          if (!connection.isClosed()) {
            logger.warn("Connection {} was not returned to store within {} seconds, closing the connection.", connection, timeout, trace);
            connection.close();
            forceClosedCounter.incrementAndGet();
          }
        } catch (SQLException e) {
          throw new OntopiaRuntimeException(e);
        }
      }, timeout, TimeUnit.SECONDS);
    }
    return connection;
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return null;
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    // no-op
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    loginTimeout = seconds;
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return loginTimeout;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLException("Not supported.");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new SQLException("Not supported.");
  }
}
