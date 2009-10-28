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
   * Get the declaration for this variable path element.
   * 
   * @return the variable declaration.
   */
  public VariableDecl getDeclaration();
}
