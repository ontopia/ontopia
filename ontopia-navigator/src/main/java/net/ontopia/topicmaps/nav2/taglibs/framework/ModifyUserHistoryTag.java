
package net.ontopia.topicmaps.nav2.taglibs.framework;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.utils.HistoryMap;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Framework related tag for modifying (means right now:
 * adding and removing) an object at the user object which is bound to
 * the session scope.
 *
 * @see net.ontopia.topicmaps.nav2.core.UserIF
 * @see javax.servlet.jsp.PageContext#findAttribute
 */
public class ModifyUserHistoryTag extends TagSupport {

  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(ModifyUserHistoryTag.class.getName());

  // constants
  protected final static String OP_ADD    = "add";
  protected final static String OP_REMOVE = "remove";

  // tag attributes
  protected String objectName;
  protected String opName;
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    // only do anything if valid user object exists
    UserIF user = FrameworkUtils.getUser(pageContext);
    HistoryMap history = user.getHistory();
    if (history != null) {
      Object obj = pageContext.findAttribute(objectName);
      // log.debug("Found object " + obj );
      if (obj != null) {
        if (opName.equals(OP_ADD))
          history.add(obj);
        else
          history.removeEntry(obj);
      } else
        log.info("Could not find object by name '" + objectName + "'.");
    } else
      log.info("No history attached to user object");
    // empty tag has not to eval anything
    return SKIP_BODY;
  }
  
  // -------------------------------------------------------
  // set methods for tag attributes
  // -------------------------------------------------------

  public void setObject(String objectName) {
    this.objectName = objectName;
  }

  public void setOperation(String opName) {
    if (opName.equals(OP_ADD) || opName.equals(OP_REMOVE))
      this.opName = opName;
    else
      throw new IllegalArgumentException("Only add and remove operation allowed.");
  }
  
}
