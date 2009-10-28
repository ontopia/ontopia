package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for variable in the AST.
 */
public abstract class AbstractVariable extends AbstractPathElement implements
    VariableIF {
  private VariableDecl decl;
  private PathElementIF.TYPE varType; 

  /**
   * Create a new variable with the given name.
   * 
   * @param name the name of the variable.
   */
  public AbstractVariable(VariableDecl decl) {
    super("VARIABLE");
    this.decl = decl;
    this.varType = TYPE.UNKNOWN;
  }

  public String getVarName() {
    return decl.getVariableName();
  }

  public VariableDecl getDeclaration() {
    return decl;
  }
  
  public TYPE getVarType() {
    return varType;
  }
  
  public void setVarType(TYPE type) {
    varType = type;
  }
  
  @Override
  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(  VARIABLE) [" + getVarName() + "]", level);
  }

  @Override
  public String toString() {
    return "$" + decl.getVariableName();
  }
}
