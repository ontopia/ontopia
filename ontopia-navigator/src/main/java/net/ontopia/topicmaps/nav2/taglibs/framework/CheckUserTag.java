
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
  public int doStartTag() throws JspTagException {
    // ensure that valid user object exists, otherwise create new one
    FrameworkUtils.getUser(pageContext);
    
    // empty tag has not to eval anything
    return SKIP_BODY;
  }

}
