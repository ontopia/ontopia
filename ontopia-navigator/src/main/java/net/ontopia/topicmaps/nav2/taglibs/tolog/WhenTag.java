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

import javax.servlet.jsp.JspTagException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Variant of IfTag, which sets a parent alerts a parent ChooseTag,
 * if it's body is evaluated.
 * If the body is evaluated parentChooser.setFoundMatchingWhen() is called.
 */
public class WhenTag extends IfTag {

  // members
  protected ChooseTag parentChooser;
  
  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    parentChooser = (ChooseTag) findAncestorWithClass(this, ChooseTag.class);
    if (parentChooser == null)
      throw new JspTagException(
              "tolog:when tag is not inside tolog:choose tag.");

    parentChooser.setFoundWhen();
    
    // If a matching when was already found within the parentChooser
    if (parentChooser.foundMatchingWhen()) {
      // No more WhenTags need to be executed (tested in each WhenTag).
      ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    
      if (contextTag == null)
        throw new JspTagException("<tolog:when> must be nested directly or"
                + " indirectly within a <tolog:context> tag, but no"
                + " <tolog:context> tag was found.");
      
      contextManager = contextTag.getContextManager();
      
      contextManager.pushScope();
      return SKIP_BODY;
    }
    
    return super.doStartTag();
  }

  /** 
   * Actions after some body has been evaluated.
   */
  @Override
  public int doAfterBody() throws JspTagException {
    parentChooser.setFoundMatchingWhen();
    
    return super.doAfterBody();
  }
  
  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    // reset members
    parentChooser = null;

    super.release();
  }
}
