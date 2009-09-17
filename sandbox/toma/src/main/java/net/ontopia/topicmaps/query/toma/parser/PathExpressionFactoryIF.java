package net.ontopia.topicmaps.query.toma.parser;

import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathRootIF;

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
   * @param name the name of the variable.
   * @return a new variable.
   */
  public PathRootIF createVariable(String name);

  /**
   * INTERNAL: Create a new Topic literal.
   * 
   * @param type the type how a topic literal is specified.
   * @param id the identifier for this topic.
   * @return a new topic literal.
   */
  public PathRootIF createTopic(String type, String id);

  /**
   * INTERNAL: Create a new empty root for certain path expressions
   * 
   * @see AbstractEmptyRoot for more details.
   * 
   * @return a new empty root.
   */
  public PathRootIF createEmptyRoot();

  /**
   * INTERNAL: Create a new any root for certain path expressions
   * 
   * @see AbstractAnyRoot for more details.
   * 
   * @return a new any root.
   */
  public PathRootIF createAnyRoot();

  /**
   * INTERNAL: Create a new path expression element based on the given name.
   * 
   * @param name the type of path element to be created.
   * @return a new path expression element.
   */
  public PathElementIF createElement(String name);
}
