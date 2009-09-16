package net.ontopia.topicmaps.query.toma.parser;

import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathRootIF;

/**
 * INTERNAL: factory to create appropriate AST path expression elements to be 
 * used for execution by the basic or rdbms QueryProcessor.
 */
public interface PathExpressionFactoryIF 
{
  /**
   * 
   * @return
   */
  public PathExpressionIF createPathExpression();
  
  /**
   * 
   * @param name
   * @return
   */
  public PathRootIF createVariable(String name);
  
  /**
   * 
   * @param type
   * @param id
   * @return
   */
  public PathRootIF createTopic(String type, String id);
  
  /**
   * 
   * @return
   */
  public PathRootIF createEmptyRoot();
  
  /**
   * 
   * @return
   */
  public PathRootIF createAnyRoot();
  
  /**
   * 
   * @param name
   * @return
   */
  public PathElementIF createElement(String name);
}
