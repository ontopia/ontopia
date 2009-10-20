package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.utils.QueryTracer;

/**
 * INTERNAL: Equality operator, checks whether two objects are equal.
 */
public class EqualsExpression extends AbstractComparisonExpression {
  public EqualsExpression() {
    super("EQUALS");
  }

  @Override
  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    if (getChildCount() != 2)
      return null;

    BasicExpressionIF left = (BasicExpressionIF) getChild(0);
    BasicExpressionIF right = (BasicExpressionIF) getChild(1);

    // Optimization:
    // If the left or right side of the expression is a sole variable
    // and the variable has not been bound yet, just bind it to the result of
    // the other expression.
    if (left instanceof PathExpression && ((PathExpression) left).isVariable()) {
      PathExpression path = (PathExpression) left;
      String name = path.getVariableName();
      if (context.getResultSet(name) == null) {
        return bindVariableToExpression(name, right, context);
      }
    } else if (right instanceof PathExpression
        && ((PathExpression) right).isVariable()) {
      PathExpression path = (PathExpression) right;
      String name = path.getVariableName();
      if (context.getResultSet(name) == null) {
        return bindVariableToExpression(name, left, context);
      }
    }

    // for normal expressions (without optimization) call the evaluate from the
    // super-class.
    return super.evaluate(context);
  }

  private ResultSet bindVariableToExpression(String name,
      BasicExpressionIF expr, LocalContext context)
      throws InvalidQueryException {
    QueryTracer.enter(this);
    
    ResultSet rs = expr.evaluate(context);
    rs.setColumnName(rs.getLastIndex(), name);
    context.addResultSet(rs);
    
    QueryTracer.leave(rs);
    return rs;
  }

  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 != null && s2 != null && s1.equals(s2))
      return true;
    else
      return false;
  }
}
