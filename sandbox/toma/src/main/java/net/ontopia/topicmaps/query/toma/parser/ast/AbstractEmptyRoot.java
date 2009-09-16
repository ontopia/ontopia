package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * Represents empty input for an association path expression within a TOMA query
 * @author tn
 */
public abstract class AbstractEmptyRoot implements PathRootIF
{
  public AbstractEmptyRoot() {}

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(     EMPTY)", level);
  }
  
  public String toString() {
    return "EMPTY";
  }
}
