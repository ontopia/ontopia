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

import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;

import net.ontopia.utils.ontojsp.JSPTreeNodeIF;

/**
 * INTERNAL: Implemented by an object which represents a executable
 * function within the navigator framework. It is comparable to a
 * macro definition and can be used as an shortcut in a
 * JSP-environment together with Ontopia's taglibs.<p>
 *
 * See logic:externalFunction for how to register your own function
 * with the tag libraries. The function can later be executed using
 * logic:call.
 */
public interface FunctionIF {

  /**
   * INTERNAL: Return the names of the parameters as an ordered
   * <code>Collection</code>.
   */
  Collection getParameters();

  /**
   * INTERNAL: Executes this function in the specified context.
   *
   * @return Collection The function return value collection. If null
   * is returned, no value will be given to the parent value accepting
   * tag.
   */
  Collection execute(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException;

  // --- deprecated methods
  
  /**
   * INTERNAL: Executes this function in the specified context.
   *
   * @deprecated 1.3.4. Use <code>Object call(PageContext)</code>
   * instead.
   */
  @Deprecated
  void call(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException;

  /**
   * INTERNAL: Gets the name of this function.
   *
   * @deprecated 1.3.4. Function names are now stored outside the
   * function object itself.
   */
  @Deprecated
  String getName();

  /**
   * INTERNAL: Gets the name of the variable to which the return value
   * of the function should be assigned to. Returns null if no return
   * variable name was specified.
   *
   * @since 1.3
   *
   * @deprecated 1.3.4. Return function value from the <code>Object
   * call(PageContext)</code> method instead.
   */
  @Deprecated
  String getReturnVariableName();
  
  /**
   * INTERNAL: Gets the rode node of this Function.
   *
   * @deprecated This method is not used, and need not be implemented.
   */
  @Deprecated
  JSPTreeNodeIF getRootNode();

  /**
   * INTERNAL: Gets the reference to the Module this function belongs
   * to.
   *
   * @deprecated This method is not used, and need not be implemented.
   */
  @Deprecated
  ModuleIF getModule();
  
}
