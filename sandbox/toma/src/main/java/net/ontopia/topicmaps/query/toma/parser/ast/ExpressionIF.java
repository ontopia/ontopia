package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.List;

import net.ontopia.topicmaps.query.toma.impl.utils.QueryOptimizerIF;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: represents a TOMA expression in the AST.
 */
public interface ExpressionIF extends ASTElementIF {
  /**
   * Adds an expression as a child to this expression.
   * 
   * @param expr the expression to be added as a child.
   * @throws AntlrWrapException if the expression is not allowed to have child
   *           expressions.
   */
  public void addChild(ExpressionIF expr) throws AntlrWrapException;

  /**
   * Get the number of child expressions.
   * 
   * @return the number of children.
   */
  public int getChildCount();

  /**
   * Get the child at the specified index.
   * 
   * @param idx the index of the child.
   * @return the child at the given index.
   */
  public ExpressionIF getChild(int idx);

  /**
   * Get the children of this expression as a {@link List}.
   * 
   * @return the list of children.
   */
  public List<ExpressionIF> getChilds();
  
  /**
   * Optimize this expression with the given optimizer.
   * 
   * @param optimizer the optimizer to be used.
   * @return the optimized expression to be used afterwards.
   */
  public ExpressionIF optimize(QueryOptimizerIF optimizer);
}
