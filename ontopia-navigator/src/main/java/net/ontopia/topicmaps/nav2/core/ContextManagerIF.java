/*
 * #!
 * Ontopia Navigator
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.nav2.core;

import java.util.Collection;

/**
 * INTERNAL: Interface which have to be implemented by classes
 * managing the context of a complete sub-hierarchy.
 */
public interface ContextManagerIF {

  /**
   * INTERNAL: Gets the current lexical scope. This object is an
   * opaque identifier that is only to be used by setValueInScope
   * for identifying the scope in which you want to set a value.
   * Clients should <strong>not</strong> make any assumptions
   * about this object, and should <strong>not</strong> try to
   * modify it or work directly with it.
   */
  public Object getCurrentScope();
  
  /**
   * INTERNAL: Gets value for specified variable name.
   * First search in current local scope, if there is
   * no such named variable, go up the lexical scope
   * hierarchy and try to retrieve there this variable.
   * If not found at all return <code>null</code>.
   *
   * @throws VariableNotSetException if value is due
   *         to a not set variable not available.
   */
  public Collection getValue(String name)
    throws VariableNotSetException;

  /**
   * INTERNAL: Gets value for specified variable name. It is the same
   * as getValue(String), except that defaultValue is returned if the
   * variable does not exist.
   *
   * @since 1.4.1
   */
  public Collection getValue(String name, Collection defaultValue);

  /**
   * INTERNAL: Add Collection with specified name to registry.
   */
  public void setValue(String name, Collection coll);

  /**
   * INTERNAL: Add Collection with specified name to registry.
   *         The object get internally transformed to a Collection.
   */
  public void setValue(String name, Object obj);

  /**
   * INTERNAL: Add Collection with specified name to to the registry
   *         identified by <code>scope</code>.
   */
  public void setValueInScope(Object scope, String name, Collection obj);

  /**
   * INTERNAL: Gets the default value in the current scope.
   *
   * @throws VariableNotSetException if value is due
   *         to a not set variable not available.
   */
  public Collection getDefaultValue() throws VariableNotSetException;
  
  /**
   * INTERNAL: Sets the default value in the current scope.
   */
  public void setDefaultValue(Collection coll);

  /**
   * INTERNAL: Sets the default value in the current scope.
   *         The object get internally transformed to a Collection.
   */
  public void setDefaultValue(Object obj);

  /**
   * INTERNAL: Pushes a new set of variables (name/collection-pairs)
   * onto the top of this stack.
   */
  public void pushScope();
  
  /**
   * INTERNAL: Removes the current set of variables at the top of this stack.
   */
  public void popScope();

  /**
   * INTERNAL: Clear all variables hold on stack.
   */
  public void clear();
  
}
