package net.ontopia.topicmaps.query.toma.parser.ast;

/**
 * INTERNAL: The variable declaration within a SelectStatement.
 */
public class VariableDecl {
  private String varName;
  
  public VariableDecl(String name) {
    this.varName = name;
  }
  
  /**
   * Get the name of the variable.
   * 
   * @return the name of the variable.
   */
  public String getVariableName() {
    return varName;
  }
}
