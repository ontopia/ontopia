package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

public class InExpression extends AbstractExpression implements BasicExpressionIF
{
  public InExpression()
  {
    super("IN");
  }

  public ResultSet evaluate(LocalContext context) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public boolean validate() throws AntlrWrapException {
    if (getChildCount() != 1) {
      throw new AntlrWrapException(
          new InvalidQueryException("expression '" + getName()
              + "' needs to have exactly one child."));
    }
    return true;
  }
}
