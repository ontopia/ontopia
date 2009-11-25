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
package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF.TYPE;

/**
 * INTERNAL: Represents a TOMA path expression in the AST. A path expression
 * consists of a root node and a path of arbitrary length.
 */
public interface PathExpressionIF extends ExpressionIF {
  /**
   * Append a path element {@link PathElementIF} to the end of the path
   * expression.
   * 
   * @param element the element to be appended.
   * @throws AntlrWrapException if the path element to be appended would create
   *           an invalid path expression.
   */
  public void addPath(PathElementIF element) throws AntlrWrapException;
  
  /**
   * Specifies the output type for this {@link PathExpressionIF}.
   * 
   * @return the {@link TYPE} that is the output of this element.
   */
  public TYPE output();
}
