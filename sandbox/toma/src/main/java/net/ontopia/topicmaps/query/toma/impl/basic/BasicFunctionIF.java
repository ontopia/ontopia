package net.ontopia.topicmaps.query.toma.impl.basic;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL:
 */
public interface BasicFunctionIF extends BasicExpressionIF {
  /**
   * 
   * @param context
   * @return
   */
  public String evaluate(Object obj) throws InvalidQueryException;
}
