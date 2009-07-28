
// $Id: ParsedQueryIF.java,v 1.8 2003/12/19 09:01:46 larsga Exp $

package net.ontopia.topicmaps.query.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * PUBLIC: Used to represent parsed queries.
 */
public interface ParsedQueryIF {

  /**
   * PUBLIC: Returns the variables in the <tt>select</tt> clause of
   * the query, in the order given there. If there is no
   * <tt>select</tt> clause all the variables used in the query are
   * returned, in no particular order.
   * @return An immutable List of String objects.
   */
  public List<String> getSelectedVariables();

  /**
   * PUBLIC: Returns the variables that are counted in the
   * <tt>select</tt> clause of the query. They are returned in no
   * particular order. If there are no counted variables, or if there
   * is no <tt>select</tt> clause an empty collection is returned.
   * @return An immutable Collection of String objects.
   */
  public Collection<String> getCountedVariables();

  /**
   * PUBLIC: Returns all the variables used in the query, in no
   * particular order.
   * @return An immutable Collection of String objects.
   */
  public Collection<String> getAllVariables();

  /**
   * PUBLIC: Returns the variables listed in the <tt>order by</tt>
   * clause in the order they are given there. In order to see which
   * ones are ascending and which descending, use the
   * <tt>isOrderedAscending</tt> method.
   * @return An immutable List of String objects.
   */
  public List<String> getOrderBy();

  /**
   * PUBLIC: Returns true if the named variable is to be sorted in
   * ascending order.
   */
  public boolean isOrderedAscending(String name);

  /**
   * PUBLIC: Executes the query, returning the query result. Query
   * results are <em>not</em> cached, so results are up to date.
   */
  public QueryResultIF execute() throws InvalidQueryException;

  /**
   * PUBLIC: Executes the query binding the parameters in the query to
   * the values given in the 'arguments' map, returning the query
   * result. Query results are <em>not</em> cached, so results are up
   * to date.
   * @since 2.0
   */
  public QueryResultIF execute(Map<String, ?> arguments) throws InvalidQueryException;
  
}
