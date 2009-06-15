// $Id: QueryIF.java,v 1.7 2005/07/12 09:37:39 grove Exp $

package net.ontopia.persistence.proxy;

import java.util.Map;
  
/**
 * INTERNAL: Interface for representing queries.
 */

public interface QueryIF {

  /**
   * INTERNAL: Executes the query without any parameters. The query
   * result is returned. The actual type of the query result is
   * specific to the query implementation.
   */
  public Object executeQuery() throws Exception;

  /**
   * INTERNAL: Executes the query with the given parameters. The query
   * result is returned. The actual type of the query result is
   * specific to the query implementation.
   */
  public Object executeQuery(Object[] params) throws Exception;

  /**
   * INTERNAL: Executes the query with the given named parameters. The
   * query result is returned. The actual type of the query result is
   * specific to the query implementation.
   */
  public Object executeQuery(Map params) throws Exception;
  
}






