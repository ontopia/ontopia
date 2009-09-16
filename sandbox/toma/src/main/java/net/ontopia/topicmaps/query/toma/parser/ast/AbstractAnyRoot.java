package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * Represents a wildcard for role types in an association path.
 */
public abstract class AbstractAnyRoot implements PathRootIF
{
  public AbstractAnyRoot() {}

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(        $$)", level);
  }
  
  public String toString() {
    return "ANY";
  }
}
