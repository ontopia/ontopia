package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF.TYPE;

/**
 * INTERNAL: Represents a TOMA path expression in the AST. A path expression
 * consists of a root node and a path of arbitrary length.
 */
public interface PathExpressionIF extends ExpressionIF {
  /**
   * Append a path element {@link PathElementIF} to the end of the path
   * expression.
   * 
   * @param element the element to be appended.
   * @throws AntlrWrapException if the path element to be appended would create
   *           an invalid path expression.
   */
  public void addPath(PathElementIF element) throws AntlrWrapException;
  
  /**
   * Specifies the output type for this {@link PathExpressionIF}.
   * 
   * @return the {@link TYPE} that is the output of this element.
   */
  public TYPE output();
}
