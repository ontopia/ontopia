package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;


public class OrExpression extends AbstractExpression implements BasicExpressionIF 
{
  public OrExpression()
  {
    super("OR");
  }

  public ResultSet evaluate(LocalContext context) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public boolean validate() throws AntlrWrapException {
    if (getChildCount() != 2) {
      throw new AntlrWrapException(
          new InvalidQueryException("expression '" + getName()
              + "' needs to have two children."));
    }
    return true;
  }
}
