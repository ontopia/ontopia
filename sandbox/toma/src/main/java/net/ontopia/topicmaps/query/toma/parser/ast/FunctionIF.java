package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: Represents a function call in the AST.
 * Syntactically, a function has an instance of {@link ExpressionIF} as child and 
 * an arbitrary number of parameters.
 */
public interface FunctionIF extends ExpressionIF {
  
  /**
   * Add a parameter, that will be used while executing the function.
   *  
   * @param param the parameter to be added.
   * @throws AntlrWrapException if the number of arguments is not valid
   * for this function.
   */
  public void addParam(String param) throws AntlrWrapException;
  
  /**
   * Returns whether this is an aggregate function or not.
   * 
   * @return true if this is an aggregate function, false otherwise.
   */
  public boolean isAggregateFunction();
}
