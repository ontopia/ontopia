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

import net.ontopia.utils.PropertyUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/** 
 * INTERNAL: Abstract connection factory implementation that holds
 * common connection properties. Used by a couple of other connection
 * factory implementations.
 */

public abstract class AbstractConnectionFactory implements ConnectionFactoryIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(AbstractConnectionFactory.class.getName());

  protected Map<String, String> properties;
  
  protected String connstring;
  protected String driver;
  protected String username;
  protected String password;

  public AbstractConnectionFactory(Map<String, String> properties) {
    this.properties = properties;
    
    driver = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.DriverClass");
    connstring = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.ConnectionString");
    
    if ((connstring != null) && connstring.startsWith("jdbspy:")) {
      loadSpyDriver();
    }

    username = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.UserName", false);
    password = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.Password", false);
  }
  
  public abstract Connection requestConnection() throws SQLException;

  public void close() {
  }
  
  protected String getConnectionString() {
    return connstring;
  }

  protected String getDriver() {
    return driver;
  }

  protected String getUserName() {
    return username;
  }

  protected String getPassword() {
    return password;
  }

  // Register jdbcspy driver
  public static void loadSpyDriver() {
    try {
      Class.forName("net.ontopia.persistence.jdbcspy.SpyDriver");
    } catch (ClassNotFoundException e) {
      // ignore if not exists
    }
  }  
}





