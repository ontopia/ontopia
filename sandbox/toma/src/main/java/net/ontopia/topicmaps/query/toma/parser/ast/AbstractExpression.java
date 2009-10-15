package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.utils.QueryOptimizerIF;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for all expressions in the TOMA AST.
 */
public abstract class AbstractExpression implements ExpressionIF {
  protected String name;
  protected ArrayList<ExpressionIF> childs;
  protected int requiredChilds;

  /**
   * Create a new expression with the given name.
   * @param name the name of the expression
   * @param reqChilds the number of required child expressions
   */
  protected AbstractExpression(String name, int reqChilds) {
    this.name = name;
    childs = new ArrayList<ExpressionIF>();
    this.requiredChilds = reqChilds;
  }

  /**
   * Get the name of the expression.
   * @return the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Replace the child at index.
   * 
   * @param index the index of the child to be replaced.
   * @param expr the expression to be used.
   */
  public void setChild(int index, ExpressionIF expr) {
    childs.set(index, expr);
  }
  
  /**
   * Add an expression as a child to this expression.
   * @param expr the expression to be added as a child.
   * @throws AntlrWrapException if this operation is not allowed. 
   */
  public void addChild(ExpressionIF expr) throws AntlrWrapException {
    childs.add(expr);
  }

  /**
   * Get the number of children.
   * @return the number of children.  
   */
  public int getChildCount() {
    return childs.size();
  }

  /**
   * Get the child expression at the given index.
   * @param idx the given index.
   * @return the nth child expression or null if outside the range.
   */
  public ExpressionIF getChild(int idx) {
    try {
      return childs.get(idx);
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  /**
   * Get a list containing all children.
   * @return a list with all children.
   */
  public List<ExpressionIF> getChilds() {
    return Collections.unmodifiableList(childs);
  }

  public boolean validate() throws AntlrWrapException {
    if (requiredChilds > 0 && getChildCount() != requiredChilds) {
      throw new AntlrWrapException(new InvalidQueryException("expression '"
          + getName() + "' has " + getChildCount() + " instead of "
          + requiredChilds + " child(s)."));
    }
    
    // validate all children
    for (ExpressionIF child : childs) {
      child.validate();
    }
    
    return true;
  }

  public ExpressionIF optimize(QueryOptimizerIF optimizer) {
    for (int i=0; i<getChildCount(); i++) {
      ExpressionIF child = getChild(i);
      setChild(i, optimizer.optimize(child));
    }
    
    return optimizer.optimize(this);
  }

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    switch (childs.size()) {
    case 0:
      buf.append(String.format("(%1$10s)", getName()), level);
      break;

    case 1:
      buf.append(String.format("(%1$10s)", getName()), level);
      childs.get(0).fillParseTree(buf, level + 1);
      break;

    case 2:
      childs.get(0).fillParseTree(buf, level + 1);
      buf.append(String.format("(%1$10s)", getName()), level);
      childs.get(1).fillParseTree(buf, level + 1);
      break;

    default:
      buf.append(String.format("(%1$10s)", getName()), level);
      for (ExpressionIF child : childs) {
        child.fillParseTree(buf, level + 1);
      }
      break;
    }
  }

  public String toString() {
    return getName();
  }
}
