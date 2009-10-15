package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for variable in the AST.
 */
public abstract class AbstractVariable extends AbstractPathElement implements
    VariableIF {
  private String varName;

  /**
   * Create a new variable with the given name.
   * 
   * @param name the name of the variable.
   */
  public AbstractVariable(String name) {
    super("VARIABLE");
    this.varName = name;
  }

  public String getVarName() {
    return varName;
  }

  public void setVarName(String name) {
    this.varName = name;
  }

  @Override
  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(  VARIABLE) [" + getVarName() + "]", level);
  }

  @Override
  public String toString() {
    return "$" + varName;
  }
}
