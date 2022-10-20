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
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.core.TopicMapIF;


/**
 * INTERNAL: The implementation of <framework:getcontext>.
 */
public class GetContextTag extends TagSupport {

  // tag attributes
  private String context;

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    
    // get Context Tag and app-wide config
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // retrieve parent tag which accepts the result of this value producing op.
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);

    UserIF user = FrameworkUtils.getUser(contextTag.getPageContext());
    UserFilterContextStore userContext = user.getFilterContext();
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null) {
      throw new NavigatorRuntimeException("GetContextTag found no topic map.");
    }

    if (userContext == null) {
      userContext = new UserFilterContextStore();
    }

    Collection result = Collections.EMPTY_SET;
    if (context != null) {
      switch (context) {
        case "basename":
          result = userContext.getScopeTopicNames(topicmap);
          break;
        case "variant":
          result = userContext.getScopeVariantNames(topicmap);
          break;
        case "association":
          result = userContext.getScopeAssociations(topicmap);
          break;
        case "occurrence":
          result = userContext.getScopeOccurrences(topicmap);
          break;
        default:
          break;
      }
    }

    // kick it over to the accepting tag
    acceptingTag.accept(result);      

    return SKIP_BODY;
  }
  
  /**
   * Overrides the parent method.
   */
  @Override
  public void release() {
    // does nothing
  }
  
  public void setContext(String var) throws NavigatorRuntimeException {
    context = var;
    if (!"basename".equals(context) &&
        !"variant".equals(context) &&
        !"occurrence".equals(context) &&
        !"association".equals(context)) {
      throw new NavigatorRuntimeException("Incorrect value ('" + var + "')" +
                                          " given for attribute 'context' in" +
                                          " element 'getcontext'.");
    }
  }

}
