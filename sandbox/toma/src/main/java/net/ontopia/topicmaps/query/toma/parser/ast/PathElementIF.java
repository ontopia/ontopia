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

import java.util.Set;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: Represents a path element of a path expression in the AST.
 */
public interface PathElementIF extends ASTElementIF {
  /**
   * Indicates the different constructs that can be the in/output of an
   * evaluated {@link PathElementIF}.
   */
  public enum TYPE {
    TOPIC, 
    ASSOCIATION, 
    NAME, 
    VARIANT, 
    OCCURRENCE, 
    LOCATOR, 
    STRING,
    NONE,
    UNKNOWN
  };

  /**
   * Use the given level for this element.
   * 
   * @param l the level to be used.
   * @throws AntlrWrapException if the element does not support levels.
   */
  public void setLevel(Level l) throws AntlrWrapException;

  /**
   * Get the specified level for this element.
   * 
   * @return the level if specified, null otherwise.
   */
  public Level getLevel();
  
  /**
   * Use the given expression as a type for this element.
   * 
   * @param expr the expression to be used as a type.
   * @throws AntlrWrapException if the element does not support types.
   */
  public void setType(PathExpressionIF expr) throws AntlrWrapException;

  /**
   * Get the type of this path element.
   * 
   * @return the specified type, null otherwise.
   */
  public PathExpressionIF getType();
  
  /**
   * Use the given expression as a scope for this element.
   * 
   * @param expr the expression to be used as a scope.
   * @throws AntlrWrapException if this element does not support scopes.
   */
  public void setScope(PathExpressionIF expr) throws AntlrWrapException;

  /**
   * Get the scope for this path element.
   * 
   * @return the specified scope, null otherwise.
   */
  public PathExpressionIF getScope();
  
  /**
   * Bind a variable to this path element.
   * 
   * @param var the variable to use for binding.
   * @throws AntlrWrapException
   */
  public void bindVariable(VariableIF var) throws AntlrWrapException;

  /**
   * Bind an input variable to this path element.
   * 
   * @param var the variable to use for binding.
   * @throws AntlrWrapException
   */
  public void bindInputVariable(VariableIF var) throws AntlrWrapException;
  
  /**
   * Add a {@link PathExpressionIF} as a child to this path element.
   * 
   * @param expr the expression to be added as a child.
   * @throws AntlrWrapException if this element does not support children.
   */
  public void addChild(PathExpressionIF expr) throws AntlrWrapException;

  /**
   * Specifies the set of valid input types for this {@link PathElementIF}.
   * 
   * @return a {@link Set} of {@link TYPE} containing valid the valid input.
   */
  public Set<TYPE> validInput();

  /**
   * Specifies the output type for this {@link PathElementIF}.
   * 
   * @return the {@link TYPE} that is the output of this element.
   */
  public TYPE output();
}
