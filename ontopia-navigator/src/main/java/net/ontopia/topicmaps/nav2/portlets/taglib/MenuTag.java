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

package net.ontopia.topicmaps.nav2.portlets.taglib;

import java.util.Collection;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.portlets.pojos.Menu;

public class MenuTag extends TagSupport {
  private String var;
  private String topic;

  @Override
  public int doStartTag() throws JspTagException {
    TopicIF topic = (TopicIF) getVariableValue(this.topic);
    if (topic == null) {
      throw new JspTagException("Couldn't find topic '" + topic + "'");
    }

    Menu menu = new Menu(topic);
    pageContext.setAttribute(var, menu, PageContext.REQUEST_SCOPE);
    
    return EVAL_BODY_INCLUDE;
  }

//   public int doAfterBody() throws JspTagException {
//     return SKIP_BODY;
//   }

//   public int doEndTag() throws JspException {
//     return EVAL_PAGE;
//   }

  @Override
  public void release() {
    // no-op
  }

  private boolean isEmpty(String value) {
    return (value == null || value.trim().equals(""));
  }

  // --- Setters

  public void setVar(String var) {
    if (isEmpty(var)) {
      this.var = null;
    } else {
      this.var = var;
    }
  }

  public void setTopic(String topic) {
    if (isEmpty(topic)) {
      this.topic = null;
    } else {
      this.topic = topic;
    }
  }
  
  // --- Internal

  private Object getVariableValue(String var) {
    // first try to access an OKS variable
    try {
      Collection coll;
      ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

      if (contextTag != null) {
        coll = contextTag.getContextManager().getValue(var);
        // FIXME: what if it's empty?
        return coll.iterator().next();
      }
    } catch (VariableNotSetException e) {
      // this is OK; we just move on to trying the page context
    }
    
    return InteractionELSupport.getValue(var, pageContext);
  }

}
