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

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Framework related tag for checking if a valid user object
 * exists in the session scope, otherwise creates a new one.
 *
 * @see net.ontopia.topicmaps.nav2.core.UserIF
 */
public class CheckUserTag extends TagSupport {

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    // ensure that valid user object exists, otherwise create new one
    FrameworkUtils.getUser(pageContext);
    
    // empty tag has not to eval anything
    return SKIP_BODY;
  }

}
