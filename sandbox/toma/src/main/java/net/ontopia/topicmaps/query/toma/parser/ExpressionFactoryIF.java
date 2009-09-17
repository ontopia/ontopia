package net.ontopia.topicmaps.query.toma.parser;

import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.FunctionIF;

/**
 * INTERNAL: factory to create appropriate AST expression elements to be used
 * for execution by the basic or rdbms QueryProcessor.
 */
public interface ExpressionFactoryIF {
  /**
   * Create a new expression.
   * 
   * @param name the name of the expression.
   * @param childs the children of the expression.
   * @return the newly created expression.
   */
  public ExpressionIF createExpression(String name, ExpressionIF... childs);

  /**
   * Create a new literal with the given value.
   * 
   * @param value the value of the literal.
   * @return the newly created literal.
   */
  public ExpressionIF createLiteral(String value);

  /**
   * Create a new function expression, identified by its name.
   * 
   * @param name the name of the function.
   * @return the newly created function.
   */
  public FunctionIF createFunction(String name);
}
