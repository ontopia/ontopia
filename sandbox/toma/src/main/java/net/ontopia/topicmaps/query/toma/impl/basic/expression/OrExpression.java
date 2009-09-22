package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;

/**
 * INTERNAL: OR expression, returns the union of the resultsets of its child
 * expressions if the two resultsets have a column in common.
 * 
 * In case, the two resultsets do not have a column in common, they are returned
 * separately.
 * 
 * TODO: better describe the relation of LocalContext and ResultSet.
 */
public class OrExpression extends AbstractBinaryExpression {
  
  public OrExpression() {
    super("OR");
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    if (getChildCount() != 2)
      return null;

    BasicExpressionIF left = (BasicExpressionIF) getChild(0);
    BasicExpressionIF right = (BasicExpressionIF) getChild(1);

    LocalContext ctx1 = new LocalContext(context.getTopicMap());
    ResultSet rs1 = left.evaluate(ctx1);

    LocalContext ctx2 = new LocalContext(context.getTopicMap());
    ResultSet rs2 = right.evaluate(ctx2);

    List<String> sharedCols = rs1.getSharedColumns(rs2);
    if (sharedCols.isEmpty()) {
      context.addResultSet(rs1);
      context.addResultSet(rs2);
      return rs2;
    } else {
      ResultSet rs = rs1.merge(rs2);
      context.addResultSet(rs);
      return rs;
    }
  }
}
