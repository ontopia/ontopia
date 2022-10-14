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

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Like a WhenTag with no condition. 
 * Evaluates its body if no earlier WhenTag was executed within
 * the parent ChooseTag.
 */
public class OtherwiseTag extends BodyTagSupport {

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
              "tolog:otherwise tag is not inside tolog:choose tag.");

    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    
    if (contextTag == null)
      throw new JspTagException("<tolog:otherwise> must be nested directly or"
              + " indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> tag was found.");
    
    contextTag.getContextManager().pushScope();
    
    // If a matching when was already found within the parentChooser
    if (parentChooser.foundMatchingWhen())
      // No more WhenTags need to be executed (tested in each WhenTag).
      return SKIP_BODY;
    return EVAL_BODY_BUFFERED;
  }

  /** 
   * Actions after some body has been evaluated.
   */
  @Override
  public int doAfterBody() throws JspTagException {
    // put out the evaluated body
    BodyContent body = getBodyContent();
    JspWriter out = body.getEnclosingWriter();
    try {
      out.print( body.getString() );
    } catch(IOException ioe) {
      throw new NavigatorRuntimeException("Error in IfTag.", ioe);
    }

    parentChooser.setFoundMatchingWhen();
    
    return SKIP_BODY;
  }
  
  /**
    * Process the end tag.
    */
  @Override
  public int doEndTag() throws JspException {
    // establish old lexical scope, back to outside of the loop
    FrameworkUtils.getContextTag(pageContext).getContextManager().popScope();

    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    // reset members
    parentChooser = null;
  }
}
