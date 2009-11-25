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
