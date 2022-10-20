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
import java.sql.SQLException;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/** 
 * INTERNAL: Connection factory that retrieves javax.sql.DataSource
 * from the JNDI environment given a JNDI name.
 */

public class JNDIConnectionFactory implements ConnectionFactoryIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(JNDIConnectionFactory.class.getName());

  protected static final String propname = "net.ontopia.topicmaps.impl.rdbms.ConnectionPool.JNDIDataSource";
  protected String jndiname;

  public JNDIConnectionFactory(String jndiname) {
    this.jndiname = jndiname;
  }
  
  public JNDIConnectionFactory(Map<String, String> properties) {    
    this.jndiname = PropertyUtils.getProperty(properties, propname);
    if (this.jndiname == null) {
      throw new OntopiaRuntimeException("Property '" + propname+ "' is not set. Please update the RDBMS properties file.");
    }
  }

  @Override
  public Connection requestConnection() throws SQLException {
    log.debug("Requesting connection from jndi pool.");

    try {
      // Obtain environment naming context
      Context initCtx = new InitialContext(); // TODO: Support other initial contexts?
      //! Context envCtx = (Context)initCtx.lookup("java:comp/env");
      
      // Look up our data source
      DataSource ds = (DataSource)initCtx.lookup(jndiname);

      // Allocate and use a connection from the pool
      Connection conn = ds.getConnection();

      // disable auto-commit
      conn.setAutoCommit(false);

      return conn;

    } catch (NamingException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public void close() {
    // Nothing to do there since we do not keep anything hanging around.
  }
  
}
