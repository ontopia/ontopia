package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * Represents a variable within a TOMA query
 * @author tn
 */
public abstract class AbstractVariable implements PathRootIF
{
  private String name;
  
  public AbstractVariable(String name)
  {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public void fillParseTree(IndentedStringBuilder buf, int level)
  {
    buf.append("(  VARIABLE) [" + getName() + "]", level);
  }
  
  public String toString()
  {
    return "$" + name;
  }
}
