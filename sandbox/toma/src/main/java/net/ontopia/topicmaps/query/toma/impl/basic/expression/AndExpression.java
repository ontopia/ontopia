package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;

public class AndExpression extends AbstractExpression implements BasicExpressionIF
{
  public AndExpression()
  {
    super("AND");
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    if (getChildCount() != 2) 
      return null;
    
    BasicExpressionIF left = (BasicExpressionIF) getChild(0);
    BasicExpressionIF right = (BasicExpressionIF) getChild(1);
      
    ResultSet rs1 = left.evaluate(context);
    ResultSet rs2 = right.evaluate(context);

    return rs2;
  }
}
