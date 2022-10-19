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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.ontopia.utils.OntopiaRuntimeException;
import org.jgroups.util.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/** 
 * INTERNAL: Connection factory that uses
 * java.sql.DriverManager.getConnection(...). This connection factory
 * does no connection pooling on its own, but one might achieve it if
 * the JDBC driver itself supports it.
 */
public class DefaultConnectionFactory extends AbstractConnectionFactory {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(DefaultConnectionFactory.class.getName());
  private static final DefaultThreadFactory tFactory = new DefaultThreadFactory("connectionCleanup-", true, true);

  protected boolean readOnly;
  protected ScheduledExecutorService connectionTimeoutExecutor = null;
  
  public DefaultConnectionFactory(Map properties, boolean readOnly) {
    super(properties);

    this.readOnly = readOnly;
    
    try {
      // load driver class
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class.forName(getDriver(), true, classLoader);
    }
    catch (ClassNotFoundException e) {
      throw new OntopiaRuntimeException("Couldn't find JDBC driver class '" + getDriver() + "' (name taken from init property net.ontopia.topicmaps.impl.rdbms.DriverClass)");
    }    

    if (timeout > -1) {
      connectionTimeoutExecutor = Executors.newSingleThreadScheduledExecutor(tFactory);
    }
  }

  @Override
  public Connection requestConnection() throws SQLException {
    log.debug("Requesting connection from default connection factory");
    
    // log.info("Database login: '" +  login + "' connstring: '" + login.getConnectionString() + "' username: '" + login.getUserName() + "'.");
    
    Connection conn;
    if (getUserName() == null || getPassword() == null) {
      Properties props = new Properties();
      props.putAll(properties);
      conn = DriverManager.getConnection(getConnectionString(), props);
    } else {
      conn = DriverManager.getConnection(getConnectionString(), getUserName(), getPassword());
    }

    // set transaction isolation level
    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    
    // set read-only flag
    conn.setReadOnly(readOnly);
    
    // disable auto-commit
    conn.setAutoCommit(false);    
    return applyTimeout(conn);
  }

  protected Connection applyTimeout(Connection connection) {
    if (timeout > -1) {
      final Exception trace = new Exception();
      connectionTimeoutExecutor.schedule(() -> {
        try {
          if (!connection.isClosed()) {
            log.warn("Connection {} was not returned to store within {} seconds, closing the connection.", connection, timeout, trace);
            connection.close();
          }
        } catch (SQLException e) {
          throw new OntopiaRuntimeException(e);
        }
      }, timeout, TimeUnit.SECONDS);
    }
    return connection;
  }

  @Override
  public void close() {
    if (connectionTimeoutExecutor != null) {
      connectionTimeoutExecutor.shutdownNow();
    }
  }
}
