
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
