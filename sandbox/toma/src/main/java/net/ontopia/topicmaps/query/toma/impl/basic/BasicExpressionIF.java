package net.ontopia.topicmaps.query.toma.impl.basic;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;

/**
 * INTERNAL:
 */
public interface BasicExpressionIF extends ExpressionIF {
  /**
   * 
   * @param context
   * @return
   */
  public ResultSet evaluate(LocalContext context) throws InvalidQueryException;
}
