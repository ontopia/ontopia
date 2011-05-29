
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

  static {
    // Register jdbcspy driver
    try {
      Class.forName("net.ontopia.persistence.jdbcspy.SpyDriver");
    } catch (ClassNotFoundException e) {
      // ignore if not exists
    }
  }

  protected Map properties;
  
  protected String connstring;
  protected String driver;
  protected String username;
  protected String password;

  public AbstractConnectionFactory(Map properties) {
    this.properties = properties;
    
    driver = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.DriverClass");
    connstring = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.ConnectionString");

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
  
}





