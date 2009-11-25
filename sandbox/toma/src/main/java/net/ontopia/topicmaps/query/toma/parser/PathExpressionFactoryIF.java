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
