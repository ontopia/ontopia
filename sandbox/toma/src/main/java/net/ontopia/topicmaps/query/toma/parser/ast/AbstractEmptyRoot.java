package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for the root node of association path
 * expressions that have no left side. 
 * 
 * Note: This class just exists to have a consistent object model in the AST.
 */
public abstract class AbstractEmptyRoot implements PathRootIF {
  public AbstractEmptyRoot() {
  }

  public boolean validate() throws AntlrWrapException {
    return true;
  }

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(     EMPTY)", level);
  }

  public String toString() {
    return "EMPTY";
  }
}
