package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: represents the starting node of a path expression in the AST 
 * (see {@link PathExpressionIF}).
 */
public interface PathRootIF 
{
  /**
   * Fills the parse tree with this expression.
   * 
   * @param buf the buffer to use.
   * @param level the current level of within the parse tree.
   */
  public void fillParseTree(IndentedStringBuilder buf, int level);
}
