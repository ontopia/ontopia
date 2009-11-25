/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.parser;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.FunctionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.VariableDecl;
import net.ontopia.topicmaps.query.toma.parser.ast.VariableIF;

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

  public PathElementIF createTopic(String type, String id)
      throws AntlrWrapException {
    PathElementIF topic = pathExpressionFactory.createTopic(type, id);
    if (topic == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create topic literal with id '" + id + "'"));
    return topic;
  }

  public VariableIF createVariable(VariableDecl decl) throws AntlrWrapException {
    VariableIF var = pathExpressionFactory.createVariable(decl);
    if (var == null)
      throw new AntlrWrapException(new InvalidQueryException(
          "unable to create variable '" + decl.getVariableName() + "'"));
    return var;
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
