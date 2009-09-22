package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;

/**
 * INTERNAL: Exists expression, returns all valid (not-null) results of
 * a specified child expression.  
 */
public class ExistsExpression extends AbstractUnaryExpression {
  
  public ExistsExpression() {
    super("EXISTS");
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    if (getChildCount() != 1)
      return null;

    BasicExpressionIF child = (BasicExpressionIF) getChild(0);
    ResultSet rs = child.evaluate(context);

    ResultSet result = new ResultSet(rs);

    for (Object r : rs) {
      Row row = (Row) r;
      Object val = row.getLastValue();
      if (val != null) {
        result.addRow(row);
      }
    }

    context.addResultSet(result);
    return result;
  }
}
