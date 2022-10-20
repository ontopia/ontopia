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

import java.util.HashMap;
import java.util.Stack;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Defines the template JSP to forward the request to, once
 * all of the PutTag strings have been stored.<p>
 * 
 * <h3>Example</h3>
 * <code>&lt;template:insert template='/views/template.jsp'&gt;</code>
 */
public class InsertTag extends TagSupport {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(InsertTag.class.getName());

  
  public final static String TEMPL_STACK_KEY = "template-stack";
  public final static String VIEW_PLACEHOLDER = "%view%";
  
  private String template;
  private Stack stack;

  @Override
  public int doStartTag() throws JspException {
    stack = getStack();
    stack.push(new HashMap());
    
    return EVAL_BODY_INCLUDE;
  }
  
  @Override
  public int doEndTag() throws JspException {
    try {
      if (log.isDebugEnabled()) {
        log.debug("doEndTag, template: '" + template + "'.");
      }
      pageContext.include(template);
    }
    catch(java.io.IOException ex) {
      throw new NavigatorRuntimeException("InsertTag: I/O Error while including " +
                                          "template '" + template + "'", ex);
    }
    catch(javax.servlet.ServletException ex) {
      throw new NavigatorRuntimeException("InsertTag: while including " +
                                          "template '" + template + "'", ex);
    }
    stack.pop();

    releaseMembers();
    return EVAL_PAGE;
  }
  
  private void releaseMembers() {
    // members
    stack = null;
    // tag attributes
    template = null;
  }
  
  public Stack getStack() {
    Stack s = (Stack) pageContext.getAttribute(TEMPL_STACK_KEY,
                                               PageContext.REQUEST_SCOPE);
    if (s == null) {
      s = new Stack();
      pageContext.setAttribute(TEMPL_STACK_KEY, s,
                               PageContext.REQUEST_SCOPE);
    }
    return s;
  }

  /**
   * Sets (according to attribute 'template') a path to the template
   * page.  <br><p>
   *
   * Note: You can use a special placeholder <code>%view%</code> if
   * you want to insert the name of the current view of the user
   * session.  This is a work-around, because JSP does not allow you
   * to use a custom tag inside another custom tag
   */
  public void setTemplate(String templateString) {
    template = templateString;
    
    // special extension for the needs of the MVS support
    // replace view placeholder with current view value
    if (template.indexOf(VIEW_PLACEHOLDER) >= 0) {
      UserIF user = FrameworkUtils.getUser(pageContext);
      String view = user.getView();
      template = StringUtils.replace(template, VIEW_PLACEHOLDER, view);
    }
  }
  
}
