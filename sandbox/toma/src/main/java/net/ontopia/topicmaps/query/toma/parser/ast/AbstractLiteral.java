package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

public abstract class AbstractLiteral extends AbstractExpression implements ExpressionIF 
{
  private String value;
  
  public AbstractLiteral(String value)
  {
    super("LITERAL");
    this.value = value;
  }

  public String getValue() 
  {
    return value;
  }

  public void setValue(String value) 
  {
    this.value = value;
  }

  public void addChild(ExpressionIF child) throws AntlrWrapException {
    throw new AntlrWrapException(
        new InvalidQueryException("Literals can not have childs"));
  }

  public void fillParseTree(IndentedStringBuilder buf, int level)
  {
    buf.append("(   LITERAL) [" + getValue() + "]", level);
  }
  
  public String toString()
  {
    return "'" + value + "'";
  }
}
