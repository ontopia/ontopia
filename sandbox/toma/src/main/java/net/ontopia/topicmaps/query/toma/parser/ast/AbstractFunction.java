package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.ArrayList;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for functions in the TOMA AST.
 */
public abstract class AbstractFunction extends AbstractExpression implements FunctionIF {

  protected ArrayList<String> parameters;
  private int maxParameters;
  private boolean isAggregate;

  /**
   * Create a new function with the given name and a maximum number of 
   * allowed parameters.
   * 
   * @param name the name of the function.
   * @param maxParameters the maximum allowed number of parameters.
   * @param aggregate if the function is an aggregate function or not.
   */
  public AbstractFunction(String name, int maxParameters, boolean aggregate) {
    super(name, 1);

    parameters = new ArrayList<String>();
    this.maxParameters = maxParameters;
    this.isAggregate = aggregate;
  }

  /**
   * Add a parameter for this function.
   * @param param the parameter to be added.
   */
  public void addParam(String param) throws AntlrWrapException {
    if (parameters.size() == maxParameters) {
      throw new AntlrWrapException(new InvalidQueryException("Function '"
          + getName() + "' has wrong number of parameters"));
    } else {
      parameters.add(param);
    }
  }

  public boolean isAggregateFunction() {
    return isAggregate;
  }

  @Override
  public void fillParseTree(IndentedStringBuilder buf, int level) {
    StringBuffer sb = new StringBuffer();

    sb.append(String.format("(%1$10s)", getName()));

    if (parameters.size() > 0) {
      sb.append(" [");
      for (String param : parameters) {
        sb.append(param);
        sb.append(",");
      }
      sb.append("]");
    }

    buf.append(sb.toString(), level);

    // a function exactly has one child.
    if (childs.size() == 1) {
      childs.get(0).fillParseTree(buf, level + 1);
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
      sb.append("'");
      sb.append(param);
      sb.append("'");
    }

    sb.append(")");

    return sb.toString();
  }
}
