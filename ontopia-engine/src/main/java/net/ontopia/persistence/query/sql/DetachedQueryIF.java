
package net.ontopia.persistence.query.sql;

import java.sql.Connection;
import java.util.Map;
  
/**
 * INTERNAL: Interface for representing shared queries.
 */

public interface DetachedQueryIF {

  /**
   * INTERNAL: Executes the query without any parameters. The query
   * result is returned. The actual type of the query result is
   * specific to the query implementation.
   */
  public Object executeQuery(Connection conn) throws Exception;

  /**
   * INTERNAL: Executes the query with the given parameters. The query
   * result is returned. The actual type of the query result is
   * specific to the query implementation.
   */
  public Object executeQuery(Connection conn, Object[] params) throws Exception;

  /**
   * INTERNAL: Executes the query with the given named parameters. The
   * query result is returned. The actual type of the query result is
   * specific to the query implementation.
   */
  public Object executeQuery(Connection conn, Map params) throws Exception;
  
}






