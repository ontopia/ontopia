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

import java.util.Collection;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.NavigatorUserException;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;

/**
 * INTERNAL: Logic Tag to allow JSP pages to signal errors.
 */
public class ErrorTag extends TagSupport  implements ValueAcceptingTagIF {

  // members
  private Collection inputCollection;
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    // evaluate first the children
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Process the end tag.
   */
  public int doEndTag() throws JspTagException {
    String collstr = (inputCollection == null ? null : inputCollection.toString());
    inputCollection = null;
    throw new NavigatorUserException("ErrorTag: signals error on page, " +
                                     "abort further page evaluation! " +
                                     "Child collection is: " + collstr);
  }


  // -----------------------------------------------------------------
  // ValueAcceptingTagIF implementation
  // -----------------------------------------------------------------
  
  public void accept(Collection value) {
    this.inputCollection = value;
  }

}





