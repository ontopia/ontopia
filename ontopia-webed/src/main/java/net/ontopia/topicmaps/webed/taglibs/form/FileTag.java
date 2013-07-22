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

import java.util.Collections;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;

import org.apache.velocity.VelocityContext;

/**
 * INTERNAL: Custom tag for input fields that allow a file to be
 * uploaded to an HTML form.
 */
public class FileTag extends BodyTagSupport {

  private static final String CATEGORY_NAME = FileTag.class.getName();
  
  /**
   * The default file name of the Velocity template.
   */
  protected final static String TEMPLATE_FILE = "file.vm";
  
  // --- Tag Attributes
  
  protected String id;
  protected String readonly;
  protected String klass;
  protected String action_name;
  protected String params;

  // --- Internal data
  protected String value;

  /**
   * Stores the body content, which becomes the value of the file
   * control.
   */
  public int doAfterBody() throws JspException {    
    if (bodyContent.getString() != null)
      value = bodyContent.getString().trim();
    else
      value = "";

    return EVAL_PAGE;
  }
  
  /**
   * Renders the input field element with its content.
   */
  public int doEndTag() throws JspException {
    // retrieve the action group
    String group_name = TagUtils.getActionGroup(pageContext);
    if (group_name == null)
      throw new JspException("file tag has no action group available.");

    VelocityContext vc = TagUtils.getVelocityContext(pageContext);

    boolean readonly = TagUtils.isComponentReadOnly(pageContext, this.readonly);
    
    // register action data and produce input field name
    if (action_name != null && !readonly) {
      String name = TagUtils.registerData(pageContext, action_name, group_name,
          params, Collections.singleton(value));
      vc.put("name", name);
    }
    
    vc.put("readonly", new Boolean(readonly));
    vc.put("value", value);

    if (id != null) vc.put("id", id);
    if (klass != null) vc.put("class", klass);
  
    // all variables are now set, proceed with outputting
    TagUtils.processWithVelocity(pageContext, TEMPLATE_FILE,
                                 pageContext.getOut(), vc);
    
    // Continue processing this page
    return EVAL_PAGE;
  }

  /**
   * Release any acquired resources.
   */
  public void release() {
    super.release();
    id = null;
    readonly = null;
    action_name = null;
    params = null;
    value = null;
  }
  
  // ------------------------------------------------------------
  // tag attribute accessors
  // ------------------------------------------------------------
  
  /**
   * Sets the id of the tag. This value will be used as the value of
   * an ID attribute in the generated output.
   */
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
   * Sets the name of the related action.
   */
  public void setAction(String action_name) {
    this.action_name = action_name;
  }
  
  /**
   * Sets the variable name(s) of the parameter(s) transmitted to the
   * action, separated by whitespaces. Specifying parameters is
   * optional.
   */
  public void setParams(String params) {
    this.params = params;
  }
    
}
