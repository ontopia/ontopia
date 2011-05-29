
package net.ontopia.topicmaps.webed.taglibs.form;

import java.util.Collections;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.utils.ContextUtils;
import net.ontopia.topicmaps.webed.impl.utils.AlwaysDifferentObject;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;
import net.ontopia.topicmaps.webed.taglibs.ActionInvokingTagIF;

import org.apache.velocity.VelocityContext;

/**
 * INTERNAL: Custom tag that invokes an action when the form is
 * submitted.
 */
public class InvokeTag extends TagSupport {

  // initialization of logging facility
  private static final String CATEGORY_NAME = InvokeTag.class.getName();
  
  /**
   * The location where the velocity template can be retrieved from.
   */
  protected final static String TEMPLATE_FILE = "invoke.vm";

  // --- Tag Attributes
  
  protected String readonly;
  protected String action_name;
  protected String params;
  protected String value_var;
  protected String run_if_no_changes;
  
  /**
   * Process the start tag, do nothing.
   */
  public int doStartTag() {
    return EVAL_BODY_INCLUDE;
  }
  
  /**
   * Generate the required input tag.
   */
  public int doEndTag() throws JspException {
    boolean readonly = TagUtils.isComponentReadOnly(pageContext, this.readonly);

    // do not do anything if component is readonly    
    if (!readonly) {     
      
      // retrieve the action group
      String group_name = TagUtils.getActionGroup(pageContext);
      if (group_name == null)
        throw new JspException("Invoke tag can't find action group.");
      
      // is there a parent tag which will invoke this action for us?
      ActionInvokingTagIF invoker = (ActionInvokingTagIF)
      findAncestorWithClass(this, ActionInvokingTagIF.class);
      
      if (invoker == null)
        writeFormControl(group_name);
      else
        passActionToInvoker(invoker, group_name);
    }

    // Continue processing this page
    return EVAL_PAGE;
  }

  /**
   * Release any acquired resources.
   */
  public void release() {
    super.release();
    readonly = null;    
    action_name = null;
    params = null;
    value_var = null;
    run_if_no_changes = null;
  }

  // ------------------------------------------------------------
  // helper methods
  // ------------------------------------------------------------

  private void passActionToInvoker(ActionInvokingTagIF invoker,
                                   String group_name)
    throws JspException {
    invoker.addAction(TagUtils.makeActionData(pageContext, action_name,
                                              group_name, params));
  }
  
  private void writeFormControl(String group_name) throws JspException {
    VelocityContext vc = TagUtils.getVelocityContext(pageContext);

    boolean runIfNoChanges = InteractionELSupport.getBooleanValue(this.run_if_no_changes, true, pageContext);
    
    // register action data and produce input field name
    Set previous = Collections.singleton(new AlwaysDifferentObject());
    String name = TagUtils.registerData(pageContext, action_name, group_name,
                                        params, previous, runIfNoChanges);
    vc.put("name", name);
    
    // get value, if any
    String value = "no-value-given";
    if (value_var != null) {
      Object thevalue = ContextUtils.getSingleValue(value_var, pageContext);
      if (thevalue != null && thevalue instanceof TMObjectIF)
        value = ((TMObjectIF) thevalue).getObjectId();
      else if (thevalue != null)
        value = thevalue.toString();
      else
        value = "null";
    }
    vc.put("value", value);

    // all variables are now set, proceed with outputting
    TagUtils.processWithVelocity(pageContext, TEMPLATE_FILE,
                                 pageContext.getOut(), vc);
  }
    
  // ------------------------------------------------------------
  // tag attribute accessors
  // ------------------------------------------------------------
  
  /**
   * Sets the the readonly flag of the tag.
   */
  public void setReadonly(String readonly) {
    this.readonly = readonly;
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
   * Sets the name of the variable used to set the value of the field.
   */
  public void setValue(String value_var) {
    this.value_var = value_var;
  }
  
  /**
   * Controls whether the action should run if no other action has
   * been run. (Useful for last-modified-* type actions.)
   */
  public void setRunIfNoChanges(String runIfNoChanges) {
    this.run_if_no_changes = runIfNoChanges;
  }

}
