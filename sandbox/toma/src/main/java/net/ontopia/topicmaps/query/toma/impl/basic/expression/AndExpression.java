package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;

/**
 * INTERNAL: AND expression, returns the intersection of the resultsets 
 * of its child expressions. 
 */
public class AndExpression extends AbstractBinaryExpression {
  
  public AndExpression() {
    super("AND");
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    if (getChildCount() != 2)
      return null;

    BasicExpressionIF left = (BasicExpressionIF) getChild(0);
    BasicExpressionIF right = (BasicExpressionIF) getChild(1);

    left.evaluate(context);
    ResultSet rs2 = right.evaluate(context);
    return rs2;
  }
}
