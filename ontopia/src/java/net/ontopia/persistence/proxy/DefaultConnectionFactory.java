
// $Id: DefaultConnectionFactory.java,v 1.12 2008/12/04 11:26:07 lars.garshol Exp $

package net.ontopia.persistence.proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import net.ontopia.utils.OntopiaRuntimeException;

import org.apache.log4j.Logger;
  
/** 
 * INTERNAL: Connection factory that uses
 * java.sql.DriverManager.getConnection(...). This connection factory
 * does no connection pooling on its own, but one might achieve it if
 * the JDBC driver itself supports it.
 */
public class DefaultConnectionFactory extends AbstractConnectionFactory {

  // Define a logging category.
  static Logger log = Logger.getLogger(DefaultConnectionFactory.class.getName());

  protected boolean readOnly;
  
  public DefaultConnectionFactory(Map properties, boolean readOnly) {
    super(properties);

    this.readOnly = readOnly;
    
    try {
      // load driver class
      Class.forName(getDriver());
    }
    catch (ClassNotFoundException e) {
      throw new OntopiaRuntimeException("Couldn't find JDBC driver class '" + getDriver() + "' (name taken from init property net.ontopia.topicmaps.impl.rdbms.DriverClass)");
    }    
  }

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
    return conn;
  }
  
}
