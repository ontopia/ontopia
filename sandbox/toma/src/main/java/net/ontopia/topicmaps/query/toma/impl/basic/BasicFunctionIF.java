package net.ontopia.topicmaps.query.toma.impl.basic;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Derived interface for functions that are being evaluated
 * by the {@link BasicQueryProcessor}.
 */
public interface BasicFunctionIF extends BasicExpressionIF {
  
  /**
   * Perform evaluation of the function on a specific input value.
   * @param obj the input value.
   * @return the result of the execution.
   */
  public String evaluate(Object obj) throws InvalidQueryException;
}
