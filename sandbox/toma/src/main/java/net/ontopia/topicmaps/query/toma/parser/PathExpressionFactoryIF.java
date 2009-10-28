package net.ontopia.topicmaps.query.toma.parser;

import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.VariableDecl;
import net.ontopia.topicmaps.query.toma.parser.ast.VariableIF;

/**
 * INTERNAL: factory to create appropriate AST path expression elements to be
 * used for execution by the basic or rdbms QueryProcessor.
 */
public interface PathExpressionFactoryIF {
  /**
   * INTERNAL: Create a new, empty path expression.
   * 
   * @return a newly created path expression
   */
  public PathExpressionIF createPathExpression();

  /**
   * INTERNAL: Create a new Variable.
   * 
   * @param decl the declaration for the variable.
   * @return a new variable.
   */
  public VariableIF createVariable(VariableDecl decl);

  /**
   * INTERNAL: Create a new Topic literal.
   * 
   * @param type the type how a topic literal is specified.
   * @param id the identifier for this topic.
   * @return a new topic literal.
   */
  public PathElementIF createTopic(String type, String id);

  /**
   * INTERNAL: Create a new path expression element based on the given name.
   * 
   * @param name the type of path element to be created.
   * @return a new path expression element.
   */
  public PathElementIF createElement(String name);
}
