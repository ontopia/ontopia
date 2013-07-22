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

package net.ontopia.topicmaps.nav2.taglibs.framework;

import java.util.Collection;
import java.util.Collections;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav.context.UserFilterContextStore;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.core.TopicMapIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The implementation of <framework:getcontext>.
 */
public class GetContextTag extends TagSupport {

  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(GetContextTag.class.getName());
  
  // tag attributes
  private String context;

  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    
    // get Context Tag and app-wide config
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // retrieve parent tag which accepts the result of this value producing op.
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);

    UserIF user = FrameworkUtils.getUser(contextTag.getPageContext());
    UserFilterContextStore userContext = user.getFilterContext();
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null)
      throw new NavigatorRuntimeException("GetContextTag found no topic map.");

    if (userContext == null)
      userContext = new UserFilterContextStore();

    Collection result = Collections.EMPTY_SET;
    if (context != null) {
      if (context.equals("basename"))
        result = userContext.getScopeTopicNames(topicmap);
      else if (context.equals("variant"))
        result = userContext.getScopeVariantNames(topicmap);
      else if (context.equals("association"))
        result = userContext.getScopeAssociations(topicmap);
      else if (context.equals("occurrence"))
        result = userContext.getScopeOccurrences(topicmap);
    }

    // kick it over to the accepting tag
    acceptingTag.accept(result);      

    return SKIP_BODY;
  }
  
  /**
   * Overrides the parent method.
   */
  public void release() {
    // does nothing
  }
  
  public void setContext(String var) throws NavigatorRuntimeException {
    context = var;
    if (!context.equals("basename") &&
        !context.equals("variant") &&
        !context.equals("occurrence") &&
        !context.equals("association"))
      throw new NavigatorRuntimeException("Incorrect value ('" + var + "')" +
                                          " given for attribute 'context' in" +
                                          " element 'getcontext'.");
  }

}
