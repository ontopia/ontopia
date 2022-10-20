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

package net.ontopia.topicmaps.nav2.taglibs.logic;

import java.io.IOException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;

/**
 * INTERNAL: Logic Tag: The else part of the if tag.
 */
public class IfElseTag extends BodyTagSupport {

  // member
  private IfTag ifTagParent;
  
  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    this.ifTagParent = (IfTag) findAncestorWithClass(this, IfTag.class);
    if (ifTagParent == null) {
      throw new JspTagException("logic:else is not inside logic:if.");
    }

    if (ifTagParent.matchCondition()) {
      return SKIP_BODY;
    } else {
      return EVAL_BODY_BUFFERED;
    }
  }
 
  /**
   * Actions after some body has been evaluated.
   */
  @Override
  public int doAfterBody() throws JspTagException {
    // we have already checked the condition in doStartTag
    try {
      BodyContent body = getBodyContent();
      JspWriter out = body.getEnclosingWriter();
      out.print( body.getString() );
      body.clearBody();
    } catch(IOException ioe) {
      throw new JspTagException("Problem occurred when writing to JspWriter in logic:else: " + ioe);
    }

    return SKIP_BODY;
  }

  @Override
  public int doEndTag() {
    // reset members
    ifTagParent = null;
    
    return EVAL_PAGE;
  }
  
  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }
  
}
