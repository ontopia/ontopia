package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for wildcard role types in association path
 * elements.
 * 
 * Note: This class just exists to have a consistent object model in the AST.
 */
public abstract class AbstractAnyRoot implements PathRootIF {
  public AbstractAnyRoot() {
  }

  public boolean validate() throws AntlrWrapException {
    return true;
  }

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(        $$)", level);
  }

  public String toString() {
    return "ANY";
  }
}
