package net.ontopia.topicmaps.query.toma.parser;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.FunctionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathRootIF;

/**
 * INTERNAL: This class is a convenience wrapper to access
 * {@link ExpressionFactoryIF} and {@PathExpressionFactoryIF} instances from 
 * within the TOMA language parser.
 * 
 * The class just consists of wrapper methods to the appropriate methods in the
 * two factories.
 */
public class LocalParseContext {
  private PathExpressionFactoryIF pathExpressionFactory;
  private ExpressionFactoryIF expressionFactory;

  public LocalParseContext(PathExpressionFactoryIF peFactory,
      ExpressionFactoryIF exFactory) {
    this.pathExpressionFactory = peFactory;
    this.expressionFactory = exFactory;
  }

  public PathExpressionIF createPathExpression() throws AntlrWrapException {
    PathExpressionIF path = pathExpressionFactory.createPathExpression();
    if (path == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create path expression"));
    return path;
  }

  public PathElementIF createElement(String name) throws AntlrWrapException {
    PathElementIF element = pathExpressionFactory.createElement(name);
    if (element == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create path element '" + name + "'"));
    return element;
  }

  public PathRootIF createTopic(String type, String id)
      throws AntlrWrapException {
    PathRootIF root = pathExpressionFactory.createTopic(type, id);
    if (root == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create topic literal with id '" + id + "'"));
    return root;
  }

  public PathRootIF createVariable(String name) throws AntlrWrapException {
    PathRootIF root = pathExpressionFactory.createVariable(name);
    if (root == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create variable '" + name + "'"));
    return root;
  }

  public PathRootIF createEmptyRoot() throws AntlrWrapException {
    PathRootIF root = pathExpressionFactory.createEmptyRoot();
    if (root == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create empty root"));
    return root;
  }

  public PathRootIF createAnyRoot() throws AntlrWrapException {
    PathRootIF root = pathExpressionFactory.createAnyRoot();
    if (root == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create wildcard root"));
    return root;
  }

  public ExpressionIF createExpression(String type, ExpressionIF... childs)
      throws AntlrWrapException {
    ExpressionIF expr = expressionFactory.createExpression(type, childs);
    if (expr == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create expression '" + type + "'"));
    return expr;
  }

  public ExpressionIF createLiteral(String value) throws AntlrWrapException {
    ExpressionIF expr = expressionFactory.createLiteral(value);
    if (expr == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create literal with value '" + value + "'"));
    return expr;
  }

  public FunctionIF createFunction(String name) throws AntlrWrapException {
    FunctionIF function = expressionFactory.createFunction(name);
    if (function == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create function '" + name + "'"));
    return function;
  }
}
