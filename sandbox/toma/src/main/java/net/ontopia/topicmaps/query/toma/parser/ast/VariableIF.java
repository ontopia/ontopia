package net.ontopia.topicmaps.query.toma.parser.ast;

/**
 * INTERNAL: Represents a variable in the AST.
 */
public interface VariableIF extends PathElementIF {
  
  /**
   * Get the name of the variable.
   * 
   * @return the name.
   */
  public String getVarName();

  /**
   * Set the name of the variable.
   * 
   * @param name the name to be set.
   */
  public void setVarName(String name);
}
