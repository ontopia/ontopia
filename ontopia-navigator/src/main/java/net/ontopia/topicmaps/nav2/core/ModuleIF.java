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

import java.net.URL;
import java.util.Collection;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

/**
 * INTERNAL: Implemented by an object which represents a module. That is
 * a collection of functions. The module is read in from a location
 * specified by an URL.
 *
 * @see net.ontopia.topicmaps.nav2.core.FunctionIF
 */
public interface ModuleIF {

  /**
   * Gets the URL from where this module was read in.
   */
  URL getURL();

  /**
   * Checks if the resource has changed in the meantime by comparing
   * the lastModified fields.
   */
  boolean hasResourceChanged();
  
  /**
   * Reads in functions contained in module from resource.
   */
  void readIn() throws NavigatorRuntimeException;

  /**
   * Removes all existing functions.
   */
  void clearFunctions();
  
  /**
   * Gets a collection of FunctionIF objects that are contained in
   * this module.
   *
   * @see net.ontopia.topicmaps.nav2.core.FunctionIF
   */
  Collection getFunctions();

  /**
   * Adds a function to this module.
   */
  void addFunction(FunctionIF func);

  /**
   * Returns a string representation of this object.
   */
  @Override
  String toString();
  
}
