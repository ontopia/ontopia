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

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A {@link ConnectionFactoryIF} backed by a {@link DataSource}.
 * @since %NEXT%
 */
public class DataSourceConnectionFactory implements ConnectionFactoryIF {
  private static final Logger log = LoggerFactory.getLogger(DataSourceConnectionFactory.class.getName());

  protected DataSource datasource;
  protected boolean readOnly;

  public DataSourceConnectionFactory(DataSource datasource, boolean readOnly) {
    this.readOnly = readOnly;
    this.datasource = datasource;
  }

  @Override
  public Connection requestConnection() throws SQLException {
    if (datasource == null) { return null; }
    log.debug("Requesting connection from {}-connectionFactory.", readOnly ? "ro" : "rw");
    Connection connection = datasource.getConnection();

    // rollback the validation transaction
    connection.rollback();

    // set flags
    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    connection.setReadOnly(readOnly);
    connection.setAutoCommit(readOnly);

    return connection;
  }

  @Override
  public void close() {
    datasource = null;
  }
}
