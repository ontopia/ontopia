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

import java.util.Collection;
import java.util.Collections;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.webed.impl.basic.ActionDataSet;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.basic.FieldInformationIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionData;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;

import org.apache.velocity.VelocityContext;

/**
 * INTERNAL: Custom tag for input fields (of type "text", "textarea",
 * "password", or "hidden") in an HTML form.
 */
public class FieldTag extends BodyTagSupport {

  // initialization of logging facility
  private static final String CATEGORY_NAME = FieldTag.class.getName();

  /**
   * The default location where the velocity template can be retrieved from.
   */
  protected final static String TEMPLATE_FILE = "field.vm";

  // --- Tag Attributes

  protected String id;
  protected String readonly;
  protected String klass;
  protected String action_name;
  protected String pattern;
  protected String params;
  protected String fieldtype_name;
  protected String trim;

  /**
   * Process the start tag, do nothing.
   * @return <code>EVAL_BODY_INCLUDE</code>
   */
  public int doStartTag() {
    return EVAL_BODY_BUFFERED;
  }

  /**
   * Renders the input field element with it's content.
   */
  public int doEndTag() throws JspException {
    // get current value
    BodyContent bodyContent = getBodyContent();
    String value = (bodyContent == null ? "" : bodyContent.getString());
    if (value == null) value = "";

    if (InteractionELSupport.getBooleanValue(this.trim, true, pageContext))
      value = value.trim();

    boolean readonly = TagUtils.isComponentReadOnly(pageContext, this.readonly);

    VelocityContext vc = TagUtils.getVelocityContext(pageContext);

    // register action data and produce input field name
    String name = null;
    if (action_name != null && !readonly) {
      // retrieve the action group
      String group_name = TagUtils.getActionGroup(pageContext);
      if (group_name == null)
        throw new JspException("field tag has no action group available.");
      
      name = TagUtils.registerData(pageContext, action_name, group_name,
          params, Collections.singleton(value));
      vc.put("name", name);

      if (pattern != null) {
        Collection regexColl = InteractionELSupport.extendedGetValue(pattern,
            pageContext);
        if (!regexColl.isEmpty()) {
          String regex = (String) regexColl.iterator().next();
          UserIF user = FrameworkUtils.getUser(pageContext);
          String requestId = TagUtils.getRequestId(pageContext);
          if (requestId != null && name != null) {
            ActionDataSet ads = (ActionDataSet)
              user.getWorkingBundle(requestId);
            ActionData data = ads.getActionData(name);
            data.setMatchExpression(regex);
          }
        }
      }
    }

    // fill in attribute values
    vc.put("readonly", new Boolean(readonly));
    vc.put("value", value);

    if (id != null) vc.put("id", id);
    if (klass != null) vc.put("class", klass);

    // get the field configuration from the application configuration
    ActionRegistryIF registry = TagUtils.getActionRegistry(pageContext);
    FieldInformationIF fieldconfig = registry.getField(fieldtype_name);

    String type = fieldconfig.getType();
    vc.put("type", type);

    // --- simple input field (type=text)
    if (type.equals("text") || type.equals("password")) {
      vc.put("maxlength", fieldconfig.getMaxLength());
      vc.put("size", fieldconfig.getColumns());
    }
    // --- multiple line input field (type=textarea)
    else if (type.equals("textarea")) {
      vc.put("cols", fieldconfig.getColumns());
      vc.put("rows", fieldconfig.getRows());
    }
    // -- hidden field (type=hidden)
    else if (type.equals("hidden"))
      ; // noop
    else {
      // could be done using separate TagExtraInfo class
      throw new JspException("field " + action_name
          + " uses unsupported type '" + type + "'.");
    }

    // all variables are now set, proceed with outputting
    JspWriter out = (bodyContent == null ? pageContext.getOut() : getBodyContent().getEnclosingWriter());
    TagUtils.processWithVelocity(pageContext, TEMPLATE_FILE, out, vc);

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
    pattern = null;
    trim = null;
    fieldtype_name = null;
  }

  // ------------------------------------------------------------
  // tag attribute accessors
  // ------------------------------------------------------------

  /**
   * Sets the id of the tag. This value will be used as the value of an ID
   * attribute in the generated output.
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
   * Sets regular expression against which value must validate.
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  /**
   * Sets the name of the input field type. This must map to a 'name' attribute
   * value of a field definition in the action configuration file.
   */
  public void setType(String fieldtype_name) {
    this.fieldtype_name = fieldtype_name;
  }

  /**
   * Sets the variable name(s) of the parameter(s) transmitted to the action,
   * separated by whitespaces. Specifying parameters is optional.
   */
  public void setParams(String params) {
    this.params = params;
  }

  /**
   * Specify whether the contained string should be trimmed for white-space,
   * i.e. whether leading and trailing spaces should be removed. Spaces will be
   * removed by default.
   */
  public void setTrim(String trim) {
    this.trim = trim;
  }

}
