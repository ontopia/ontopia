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

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Tolog Tag for evaluating a sequence of child WhenTags.
 */
public class ChooseTag extends BodyTagSupport {

  // members
  private boolean foundMatchingWhen;
  private boolean foundWhen;
    
  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    
    if (contextTag == null) {
      throw new JspTagException("<tolog:choose> must be nested directly or"
              + " indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> tag was found.");
    }
    
    contextTag.getContextManager().pushScope();
            
    this.foundMatchingWhen = false;
    
    return EVAL_BODY_INCLUDE;
  }
  
  /**
   * Process the end tag.
   */
  @Override
  public int doEndTag() throws JspException {
    // establish old lexical scope, back to outside of the loop
    FrameworkUtils.getContextTag(pageContext).getContextManager().popScope();

    if (!foundWhen()) {
      throw new JspTagException("<tolog:choose> : must have one or more"
              + " <tolog:when> tags nested within it, but none were found.\n");
    }
    
    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    foundMatchingWhen = false;
  }
  
  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------
  
  public boolean foundMatchingWhen() {
    return foundMatchingWhen;
  }

  public void setFoundMatchingWhen() {
    this.foundMatchingWhen = true;
  }

  public boolean foundWhen() {
    return foundWhen;
  }
  
  public void setFoundWhen() {
    this.foundWhen = true;
  }  
}
