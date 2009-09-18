package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF.TYPE;

/**
 * INTERNAL: represents the starting node of a path expression in the AST (see
 * {@link PathExpressionIF}).
 */
public interface PathRootIF extends ASTElementIF {
  /**
   * Specifies the output type for this {@link PathRootIF}.
   * 
   * @return the {@link TYPE} that is the output of this element.
   */
  public TYPE output();
}
