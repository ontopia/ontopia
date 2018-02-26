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

package net.ontopia.topicmaps.nav2.impl.basic;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.utils.ontojsp.JSPTreeNodeIF;
import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.core.ModuleIF;
import net.ontopia.topicmaps.nav2.utils.ContextUtils;

/**
 * INTERNAL: Abstract implementation of FunctionIF, which should be the
 * superclass for customized functions.<p>
 *
 * Subclasses of this abstract function only have to implement the
 * <code>getParameters()</code> and <code>execute()</code> methods.<p>
 *
 * @since 1.3.4
 */
public abstract class AbstractFunction implements FunctionIF {
  
  // -------------------------------------------------------
  // FunctionIF partial implementation
  // -------------------------------------------------------

  /** @return Empty list */
  @Override
  public Collection getParameters() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public Collection execute(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException {
    // Delegate to deprecated call method.
    call(pageContext, callingTag);
    // Return value
    String retvar = getReturnVariableName();
    if (retvar != null) {
      // Note: may throw VariableNotSetException if variable not set.
      return ContextUtils.getValue(retvar, pageContext);
    }
    return null;
  }

  // --- deprecated methods
  
  @Override
  public void call(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException {
    // No implementation
  }

  /** @return null */
  @Override
  public String getName() {
    return null;
  }
  
  /** @return null */
  @Override
  public String getReturnVariableName() {
    return null;
  }

  /** @return null */
  @Override
  public ModuleIF getModule() {
    return null;
  }

  /** @return null */
  @Override
  public JSPTreeNodeIF getRootNode() {
    return null;
  }
  
  // -------------------------------------------------------
  // overwrite Object implementation 
  // -------------------------------------------------------

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(64);
    sb.append("[Function: ")
      .append( getName() ).append(", params ")
      .append( getParameters() ).append( "]");    
    return sb.toString();
  }

}
