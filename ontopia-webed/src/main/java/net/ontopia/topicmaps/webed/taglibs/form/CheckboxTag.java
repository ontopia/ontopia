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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.webed.impl.utils.ActionData;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;
import net.ontopia.topicmaps.webed.taglibs.ActionInvokingTagIF;

import org.apache.velocity.VelocityContext;

/**
 * INTERNAL: Custom tag that represents a checkbox in an input form.
 */
public class CheckboxTag extends TagSupport implements ActionInvokingTagIF {
  
  // initialization of logging facility
  private static final String CATEGORY_NAME = CheckboxTag.class.getName();
  
  /**
   * The location where the velocity template can be retrieved from.
   */
  protected final static String TEMPLATE_FILE = "checkbox.vm";

  // --- Tag Attributes

  protected String id;
  protected String readonly;  
  protected String klass;
  protected String action_name;
  protected String params;
  protected String state_var;
  protected List sub_actions = new ArrayList();
  
  /**
   * Process the start tag, do nothing.
   */
  @Override
  public int doStartTag() {
    return EVAL_BODY_INCLUDE;
  }
  
  /**
   * Generate the required input tag.
   */
  @Override
  public int doEndTag() throws JspException {
    VelocityContext vc = TagUtils.getVelocityContext(pageContext);

    // check state
    boolean state = false;
    if (state_var != null) {
      Collection coll = InteractionELSupport
          .extendedGetValue(state_var, pageContext);
      if (coll != null)
        state = !coll.isEmpty();
    }
    // store current value
    Set value = new HashSet();
    if (state)
      value.add("on");
    else
      value.add(null);

    boolean readonly = TagUtils.isComponentReadOnly(pageContext, this.readonly);
    
    // register action data and produce input field name
    if (action_name != null && !readonly) {
    // retrieve the action group
      String group_name = TagUtils.getActionGroup(pageContext);
      if (group_name == null)
        throw new JspException("Checkbox tag can't find action group.");
    
      String name = TagUtils.registerData(pageContext, action_name, group_name, 
          params, sub_actions, value);
      vc.put("name", name);
    }

    if (state)
      vc.put("checked", "checked");
    else
      vc.put("checked", "");      

    if (id != null) vc.put("id", id);
    vc.put("readonly", readonly);
    if (klass != null) vc.put("class", klass);
    
    sub_actions = new ArrayList(); // we've used these now, can't retain them

    // all variables are now set, proceed with outputting
    TagUtils.processWithVelocity(pageContext, TEMPLATE_FILE, pageContext.getOut(), vc);
    
    // Continue processing this page
    return EVAL_PAGE;
  }

  /**
   * Release any acquired resources.
   */
  @Override
  public void release() {
    super.release();
    id = null;
    readonly = null;
    action_name = null;
    params = null;
    state_var = null;
    sub_actions = new ArrayList();
  }
  
  // ------------------------------------------------------------
  // ActionInvokingTagIF
  // ------------------------------------------------------------

  @Override
  public void addAction(ActionData action) {
    sub_actions.add(action);
  }
  
  // ------------------------------------------------------------
  // tag attribute accessors
  // ------------------------------------------------------------
  
  /**
   * Sets the id of the tag. This value will be used as the value of
   * an ID attribute in the generated output.
   */
  @Override
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Sets the the readonly flag of the tag.
   */
  public void setReadonly(String readonly) {
    this.readonly = readonly;
  }
  
  /**
   * Sets the class attribute of the tag. This value will be used as
   * the value of the 'class' attribute in the generated output.
   */
  public void setClass(String klass) {
    this.klass = klass;
  }

  /**
   * Sets the name of the related action (required).
   */
  public void setAction(String action_name) {
    this.action_name = action_name;
  }

  /**
   * Sets the variable name(s) of the parameter(s) transmitted to the
   * action separated by whitespaces (optional).
   */
  public void setParams(String params) {
    this.params = params;
  }
  
  /**
   * Sets the name of the variable used to determine whether the checbox
   * should be checked or not.
   */
  public void setState(String state_var) {
    this.state_var = state_var;
  }

}
