package net.ontopia.topicmaps.query.toma.impl.basic;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;

/**
 * INTERNAL: Derived interface for expressions that are being evaluated
 * by the {@link BasicQueryProcessor}.
 */
public interface BasicExpressionIF extends ExpressionIF {
  /**
   * Evaluate the expression based on the local context.
   * @param context the local context to be used for evaluation.
   * @return the result of the evaluation as a {@link ResultSet}.
   */
  public ResultSet evaluate(LocalContext context) throws InvalidQueryException;
}
