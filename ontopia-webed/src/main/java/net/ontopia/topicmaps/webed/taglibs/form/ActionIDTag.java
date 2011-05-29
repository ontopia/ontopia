
package net.ontopia.topicmaps.webed.taglibs.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.webed.impl.basic.ActionDataSet;
import net.ontopia.topicmaps.webed.impl.utils.ActionData;
import net.ontopia.topicmaps.webed.impl.utils.AlwaysDifferentObject;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;
import net.ontopia.topicmaps.webed.taglibs.ActionInvokingTagIF;

/**
 * INTERNAL: Custom tag used to generate an input field name that connects to a
 * specific action.
 */
public class ActionIDTag extends TagSupport implements ActionInvokingTagIF {

  // --- Tag Attributes

  protected String action_name;
  protected String params;
  protected String value_var;
  protected String control;
  protected List sub_actions = new ArrayList();
  protected String pattern;

  /**
   * Process the start tag, do nothing.
   * 
   * @return <code>EVAL_BODY_INCLUDE</code>
   */
  public int doStartTag() {
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Generate the required select tag.
   * 
   * @exception JspException
   *              if a JSP exception has occurred
   */
  public int doEndTag() throws JspException {
    // to nothing if form is read-only
    if (TagUtils.isFormReadOnly(pageContext.getRequest())) return EVAL_PAGE;
    
    // retrieve the action group
    String group_name = group_name = TagUtils.getActionGroup(pageContext);
    if (group_name == null)
      throw new JspException("action id tag has no action group available.");

    // figure out default value
    Set value = null;
    if (value_var != null) {
      // get value and translate objects to ids
      Collection _value = InteractionELSupport.extendedGetValue(value_var, pageContext);
      value = new HashSet(_value.size());
      Iterator iter = _value.iterator();
      while (iter.hasNext()) {
        Object o = iter.next();
        if (o instanceof TMObjectIF)
          value.add(((TMObjectIF)o).getObjectId());
        else
          value.add(o);
      }
    }

    // <FIXME>
    // this code sucks. it's hard to read, and hard to see what the
    // combination of control and value does.   
    if (control != null && (value == null || value.isEmpty())) {
      if (control.equals("list"))
        value = Collections.singleton("-1");
      else
        value = Collections.singleton(null);
    }

    // FIXME: what if control explicitly set to button?
    if (control == null) // the default is button (the doc says)
      value = Collections.singleton(null); // this is the default for buttons
    else if (value == null)
      value = Collections.singleton(new AlwaysDifferentObject());
    else if (value.isEmpty())
      value = Collections.singleton(null);
    // </FIXME>

    // register action data and produce input field name
    String name = TagUtils.registerData(pageContext, action_name, group_name,
        params, sub_actions, value);

    if (pattern != null) {
      Collection regexColl = InteractionELSupport.extendedGetValue(pattern,
          pageContext);
      if (!regexColl.isEmpty()) {
        String regex = (String) regexColl.iterator().next();
        UserIF user = FrameworkUtils.getUser(pageContext);
        String requestId = TagUtils.getRequestId(pageContext);
        if (requestId != null && name != null) {
          ActionDataSet ads = (ActionDataSet) user.getWorkingBundle(requestId);
          ActionData data = ads.getActionData(name);
          data.setMatchExpression(regex);
        }
      }
    }

    sub_actions = new ArrayList(); // we've used these now, can't retain them

    JspWriter out = pageContext.getOut();
    try {
      out.print(name);
    } catch (IOException e) {
      throw new JspException(e);
    }

    // Continue processing this page
    return EVAL_PAGE;
  }

  /**
   * Release any acquired resources.
   */
  public void release() {
    super.release();
    action_name = null;
    sub_actions = new ArrayList();
  }

  // ------------------------------------------------------------
  // tag attribute accessors
  // ------------------------------------------------------------

  /**
   * Sets the name of the related action (required).
   */
  public void setAction(String action_name) {
    this.action_name = action_name;
  }

  /**
   * Sets the name parameters of the action.
   */
  public void setParams(String params) {
    this.params = params;
  }

  /**
   * Sets the existing value of the action.
   */
  public void setValue(String value) {
    this.value_var = value;
  }

  /**
   * Sets the name of the related action.
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  /**
   * Sets the control type the action is used with.
   */
  public void setControl(String control) {
    this.control = control;
  }

  // ------------------------------------------------------------
  // ActionInvokingTagIF
  // ------------------------------------------------------------

  public void addAction(ActionData action) {
    sub_actions.add(action);
  }
}
