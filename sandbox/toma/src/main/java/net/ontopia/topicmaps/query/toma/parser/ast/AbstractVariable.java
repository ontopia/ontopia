package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for variable in the AST.
 */
public abstract class AbstractVariable implements PathRootIF {
  private String name;

  /**
   * Create a new variable with the given name.
   * 
   * @param name the name of the variable.
   */
  public AbstractVariable(String name) {
    this.name = name;
  }

  /**
   * Get the name of the variable.
   * 
   * @return the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of the variable.
   * 
   * @param name the name to be set.
   */
  public void setName(String name) {
    this.name = name;
  }

  public boolean validate() throws AntlrWrapException {
    return true;
  }

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(  VARIABLE) [" + getName() + "]", level);
  }

  public String toString() {
    return "$" + name;
  }
}
