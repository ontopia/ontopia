package net.ontopia.topicmaps.query.toma.impl.utils;

import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;

/**
 * PUBLIC: Interface for a QueryOptimizer. Used to optimize a query.
 */
public interface QueryOptimizerIF {
  /**
   * Optimize an expression.
   * 
   * @param expr the expression to be optimized.
   * @return an optimized expression.
   */
  public ExpressionIF optimize(ExpressionIF expr);
}
