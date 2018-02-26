/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.taglibs.form;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.webed.impl.utils.TagUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Enclosing tag to specify which action group the nested
 * input elements belong to.
 */
public class ActionGroupTag extends TagSupport {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(ActionGroupTag.class.getName());

  // tag attributes
  private String actiongroup_name;
  
  /**
   *
   * @return TagSupport#EVAL_BODY_INCLUDE
   */
  @Override
  public int doStartTag() throws JspException {
    if (actiongroup_name != null)
      TagUtils.setActionGroup(pageContext, actiongroup_name);
    else
      log.warn("No action group name available.");

    // Continue processing the body
    return EVAL_BODY_INCLUDE;
  }
  
  /**
   * Releases any acquired resources.
   */
  @Override
  public void release() {
    actiongroup_name = null;
    super.release();
  }

  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------
  
  public void setName(String actiongroup_name) {
    this.actiongroup_name = actiongroup_name;
  }
  
}
