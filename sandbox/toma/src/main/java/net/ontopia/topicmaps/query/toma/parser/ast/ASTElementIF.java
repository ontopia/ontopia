package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Common base interface for all elements of the AST. This contains
 * just a common method for printing the AST on screen.  
 */
public interface ASTElementIF {

  /**
   * Validates if this element is syntactically correct.
   * @return true if validation was successful.
   * @throws AntlrWrapException if an syntax error was found.
   */
  public boolean validate() throws AntlrWrapException;

  /**
   * Fills the parse tree with a string representation of this AST element.
   * 
   * @param buf the buffer to use.
   * @param level the current level of within the parse tree.
   */
  public void fillParseTree(IndentedStringBuilder buf, int level);
}
