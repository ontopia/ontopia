
package net.ontopia.persistence.proxy;

import java.sql.Connection;
import java.sql.SQLException;

/** 
 * INTERNAL: An interface for creating/requesting new JDBC connection
 * object.
 */

public interface ConnectionFactoryIF {

  /**
   * INTERNAL: Requests a new connection object.
   */
  public Connection requestConnection() throws SQLException;

  /**
   * INTERNAL: Closes the connection factory.
   */
  public void close();
  
}





