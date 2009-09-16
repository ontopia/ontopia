package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.List;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: represents an TOMA expression in the AST.
 */
public interface ExpressionIF 
{
  /**
   * Adds a child expression to this expression.
   * 
   * @param child the child to be added.
   * @throws AntlrWrapException if the expression is not allowed to 
   *         have child expressions.
   */
  public void addChild(ExpressionIF child) throws AntlrWrapException;
  
  public int getChildCount();
  
  public ExpressionIF getChild(int idx);
  
  public List<ExpressionIF> getChilds();
  
  /**
   * Fills the parse tree with this expression.
   * 
   * @param buf the buffer to use.
   * @param level the current level of within the parse tree.
   */
  public void fillParseTree(IndentedStringBuilder buf, int level);
}
