
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;

/**
 * INTERNAL: Represents a predicate in the rdbms implementation.
 */
public interface JDOPredicateIF extends BasicPredicateIF {

  /**
   * INTERNAL:
   */
  public boolean isRecursive();

  /**
   * INTERNAL: This method will be called before building the
   * query. It is used mainly for analyzing the predicate.
   */
  public void prescan(QueryBuilder builder, List arguments);
  
  /**
   * INTERNAL: Registers JDOExpressionsIF for this predicate with the
   * query builder.
   *
   * @return true if predicate was mapped to JDO expression; false if
   * the predicate could not be mapped to an JDO expression.
   */
  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException;
  
}
