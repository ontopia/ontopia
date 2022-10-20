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

package net.ontopia.topicmaps.nav.taglibs.template;

import java.util.Map;
import java.util.Stack;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

/**
 * INTERNAL: Code-sharing abstract superclass for GetTag and PutTag.
 */
public abstract class AbstractTemplateTag extends BodyTagSupport {
  protected String name;

  protected void putParameter(String content, boolean direct) throws JspException {
    
    InsertTag parent = (InsertTag) findAncestorWithClass(this, InsertTag.class);
    if (parent == null) {
      throw new JspException(this.getClass().getName() + ": has no template:insert ancestor.");
    }

    Stack template_stack = parent.getStack();
    if (template_stack == null) {
      throw new JspException(this.getClass().getName() + ": has no template stack.");
    } 

    Map params = (Map) template_stack.peek();
    if (params == null) {
      throw new JspException(this.getClass().getName() + ": has no parameter map.");
    }      
                
    params.put(name, new PageParameter(content, direct));
  }
  
  protected PageParameter getParameter() throws JspException, NavigatorRuntimeException {
    Stack stack = (Stack) pageContext
      .getAttribute(InsertTag.TEMPL_STACK_KEY, PageContext.REQUEST_SCOPE);
    if (stack == null) {
      throw new JspException(this.getClass().getName() + " has no template stack.");
    }

    Map params = (Map) stack.peek();
    if (params == null) {
      throw new JspException(this.getClass().getName() + " has no parameter map.");
    }
                        
    return (PageParameter) params.get(name);
  }
}
