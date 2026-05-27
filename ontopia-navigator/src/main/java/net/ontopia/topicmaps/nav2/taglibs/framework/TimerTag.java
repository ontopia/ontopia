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

import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Framework related tag for logging information about
 * CPU time usage to log4j.
 */
public final class TimerTag extends TagSupport {

  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(TimerTag.class.getName());

  // members
  private long startTime;
  
  // tag attributes
  private String name = "";

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    startTime = System.currentTimeMillis();
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Process the end tag for this instance.
   */
  @Override
  public int doEndTag() throws JspTagException {
    log.debug(name + ": " + (System.currentTimeMillis() - startTime));
    return EVAL_PAGE;
  }

  
  // -----------------------------------------------------------------
  // set methods
  // -----------------------------------------------------------------

  /**
   * setting the tag attribute name: helps to give the output
   * a more understandable description.
   */
  public void setName(String name) {
    this.name = name;
  }
  
}
