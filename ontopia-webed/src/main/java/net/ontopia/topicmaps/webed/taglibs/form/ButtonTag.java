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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.basic.ImageInformationIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionData;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;
import net.ontopia.topicmaps.webed.taglibs.ActionInvokingTagIF;
import net.ontopia.utils.StringUtils;

import org.apache.velocity.VelocityContext;

/**
 * INTERNAL: Custom tag that represents an graphical submit button in
 * an input form.
 */
public class ButtonTag extends TagSupport implements ActionInvokingTagIF {

  // initialization of logging facility
  private static final String CATEGORY_NAME = ButtonTag.class.getName();
  
  /**
   * The location where the velocity template can be retrieved from.
   */
  protected final static String TEMPLATE_FILE = "button.vm";

  // --- Tag Attributes
  
  protected String id;
  protected String readonly;
  protected String klass;
  protected String action_name;
  protected String params;
  protected String image_name;
  protected String button_text;
  protected String reset_button;
  protected List sub_actions = new ArrayList();
  
  /**
   * Process the start tag, do nothing.
   * @return <code>EVAL_BODY_INCLUDE</code>
   */
  public int doStartTag() {
    return EVAL_BODY_INCLUDE;
  }
  
  /**
   * Generate the button.
   */
  public int doEndTag() throws JspException {
    VelocityContext vc = TagUtils.getVelocityContext(pageContext);

    Set value = Collections.singleton(null);
    
    boolean readonly = TagUtils.isComponentReadOnly(pageContext, this.readonly);
    
    // register action data and produce input field name
    if (action_name != null && !readonly)  {
      // retrieve the action group
      String group_name = TagUtils.getActionGroup(pageContext);
      if (group_name == null)
        throw new JspException("webed:button tag has no action group");
      
      String name = TagUtils.registerData(pageContext, action_name, group_name,
                                          params, sub_actions, value);
      vc.put("name", name);
    }

    if (id != null) vc.put("id", id);
    vc.put("readonly", new Boolean(readonly));
    if (klass != null) vc.put("class", klass);

    sub_actions = new ArrayList(); // we've used these now, can't retain them

    // --- graphical button
    if (image_name != null) {
      // get the image from the application configuration
      ActionRegistryIF registry = TagUtils.getActionRegistry(pageContext);
      vc.put("type", "image");
      vc.put("title", StringUtils.escapeHTMLEntities(button_text));
      if (registry.hasImage(image_name)) {
        ImageInformationIF image = registry.getImage(image_name);
        vc.put("src", image.getRelativeURL());
        vc.put("border", (image.getBorder() == null ? "0" : image.getBorder()));
        vc.put("align", image.getAlign());
        vc.put("width", image.getWidth());
        vc.put("height", image.getHeight());
      } else {
        // If the image_name is not defined as an image in the actions.xml
        // ButtonMap, then assume that it is a URL to the image.
        vc.put("src", image_name);
      }
    }
    // --- text button
    else {
      boolean reset_button = InteractionELSupport.getBooleanValue(this.reset_button, false, pageContext);

      if (reset_button)
        vc.put("type", "reset");
      else
        vc.put("type", "submit");
      vc.put("value", StringUtils.escapeHTMLEntities(button_text));
    }
    
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
    image_name = null;
    button_text = null;
    reset_button = null;
    sub_actions = new ArrayList();
  }

  // ------------------------------------------------------------
  // ActionInvokingTagIF
  // ------------------------------------------------------------

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
   * Sets the text that should be displayed inside the button.
   * <p>Note: 'image' OR 'text' property has to be specified.
   */
  public void setText(String button_text) {
    this.button_text = button_text;
  }
  
  /**
   * Sets the name of the image that should be displayed as graphical
   * representation of this button.
   * <p>Note: 'image' OR 'text' property has to be specified.
   */
  public void setImage(String image_name) {
    this.image_name = image_name;
  }
  
  /**
   * Sets the button behaviour to reset (allowed values are
   * "yes" and "no").
   * <p>Note: If 'reset' is not set, "submit" will be the default
   * behaviour.
   */
  public void setReset(String reset_button) {
    this.reset_button = reset_button;
  }

}
