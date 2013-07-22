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

import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;

/**
 * INTERNAL: Interface which should be implemented by root-ish tags
 * handling the outermost lexical scope in which all further
 * computation happens. (In practice, this means ContextTag.)
 */
public interface NavigatorPageIF {

  /**
   * INTERNAL: Get the Context Manager.
   *
   * @return object which implements the ContextManagerIF interface
   */
  public ContextManagerIF getContextManager();
  
  /**
   * INTERNAL: Add a function to the registry.
   *
   * @param function The function to be added to the internal registry;
   *                 An object implementing FunctionIF.
   *
   * @deprecated 1.3.4. Replaced by registerFunction(String, Function).
   */
  public void registerFunction(FunctionIF function);
  
  /**
   * INTERNAL: Add a named function to the registry.
   *
   * @param name The name with which the function is to be registered.
   * @param function The function to be added to the internal registry;
   *                 An object implementing FunctionIF.
   *
   * @since 1.3.4
   */
  public void registerFunction(String name, FunctionIF function);

  /**
   * INTERNAL: Get a function out of the register.
   *
   * @param name The string which identifies the name of the function.
   *
   * @return The function
   */
  public FunctionIF getFunction(String name);

  /**
   * INTERNAL: Get the navigator application which allows
   * accessing all kind of configuration.
   *
   * @return Object implementing NavigatorApplicationIF
   */
  public NavigatorApplicationIF getNavigatorApplication();

  /**
   * INTERNAL: Get the navigator configuration for getting access
   * to the application.xml settings.
   */
  public NavigatorConfigurationIF getNavigatorConfiguration();
  
  /**
   * INTERNAL: Get the topicmap object the context tag is working with.
   *
   * @return Object implementing TopicMapIF
   */
  public net.ontopia.topicmaps.core.TopicMapIF getTopicMap();
  
  /**
   * INTERNAL: Get the tolog query processor the context tag is working with.
   *
   * @return Object implementing QueryProcessorIF
   */
  public QueryProcessorIF getQueryProcessor();

  /**
   * INTERNAL: Gets the JSP page context which allows to have access
   * to further request relevant information.
   *
   * @since 1.3.2
   */
  public javax.servlet.jsp.PageContext getPageContext();

  /**
   * INTERNAL: Gets the tolog declaration context.
   */
  public DeclarationContextIF getDeclarationContext();
}
