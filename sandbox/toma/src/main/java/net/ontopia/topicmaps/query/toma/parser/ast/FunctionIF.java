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
