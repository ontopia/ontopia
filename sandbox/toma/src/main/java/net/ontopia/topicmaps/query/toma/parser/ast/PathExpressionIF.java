package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: represents a TOMA path expression in the AST.
 */
public interface PathExpressionIF extends ExpressionIF
{
  /**
   * Set the root node of this path expression.
   * 
   * @param root the root node to be set.
   */
  public void setRoot(PathRootIF root);
  
  /**
   * Get the root node of this path expression.
   * 
   * @return the root node of the path.
   */
  public PathRootIF getRoot();
  
  /**
   * 
   * @param path
   * @throws AntlrWrapException
   */
  public void addPath(PathElementIF path) throws AntlrWrapException;
}
