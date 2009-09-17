package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: Represents a TOMA path expression in the AST. A path expression
 * consists of a root node and a path of arbitrary length.
 */
public interface PathExpressionIF extends ExpressionIF {
  /**
   * Set the root node for this path expression.
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
   * Append a path element {@link PathElementIF} to the end of the path
   * expression.
   * 
   * @param element the element to be appended.
   * @throws AntlrWrapException if the path element to be appended would create
   *           an invalid path expression.
   */
  public void addPath(PathElementIF element) throws AntlrWrapException;
}
