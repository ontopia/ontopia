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

/**
 * INTERNAL: Represents a variable in the AST.
 */
public interface VariableIF extends PathElementIF {
  
  /**
   * Get the name of the variable.
   * 
   * @return the name.
   */
  public String getVarName();
  
  /**
   * Get the declaration for this variable path element.
   * 
   * @return the variable declaration.
   */
  public VariableDecl getDeclaration();
}
