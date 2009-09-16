package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.ArrayList;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

public class AbstractFunction extends AbstractExpression implements FunctionIF {
  
  protected ArrayList<String> parameters;
  private int maxParameters;
  
  public AbstractFunction(String name, int maxParameters) {
    super(name);
    
    parameters = new ArrayList<String>();
    this.maxParameters = maxParameters;
  }

  public void addParam(String param) throws AntlrWrapException {
    if (parameters.size() == maxParameters) {
      throw new AntlrWrapException(
          new InvalidQueryException("Function '" + getName() + "' has wrong number of parameters"));
    } else {
      parameters.add(param);
    }
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    
    sb.append(getName());
    sb.append("(");

    for (ExpressionIF child : childs) {
      sb.append(child.toString());
    }
    
    for (String param : parameters) {
      sb.append(",");
      sb.append(param);
    }
    
    sb.append(")");
    
    return sb.toString();
  }
}
