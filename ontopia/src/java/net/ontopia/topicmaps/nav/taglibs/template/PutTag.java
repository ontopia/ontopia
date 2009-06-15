
// $Id: PutTag.java,v 1.23 2004/12/05 15:23:56 grove Exp $

package net.ontopia.topicmaps.nav.taglibs.template;

import java.util.Map;
import java.util.Stack;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.apache.log4j.Logger;

/**
 * INTERNAL: Labels a string on a JSP content (model) page which can be
 * used by GetTag for use on a template (view) page.
 * 
 * <h3>Examples</h3>
 * <li><h4>Including the body:</h4>
 * <code>
 * &lt;template:put name='title'&gt;My Title&lt;/template:put&gt;
 * </code></li>
 *
 * <li><h4>Including the value of the "content" attribute:</h4>
 * <code>
 * &lt;template:put name='title' direct="true" content='My Title'/&gt;
 * </code></li>
 *
 * <li><h4>Including a file:</h4>
 * <code>
 * &lt;template:put name='title' content='/fragments/mytitle.txt'/&gt;
 * </code></li>
 */
public class PutTag extends AbstractTemplateTag {
  
  // Define a logging category.
  static Logger log = Logger.getLogger(PutTag.class.getName());

  // tag attributes
  private String content;
  private boolean direct;

  public int doStartTag() throws JspException {
    if (content != null) {
      if (log.isDebugEnabled())
        log.debug("doStartTag: register variable '"+name+"'.");

      putParameter(content, direct);
      
      return SKIP_BODY;
    } else {
      if (log.isDebugEnabled())
        log.debug("doStartTag: evaluate body");
      return EVAL_BODY_BUFFERED;
    }
  }

  public int doAfterBody() throws JspException {
    BodyContent bodyContent = getBodyContent();
    String content = bodyContent.getString();

    // save body content into params after evaluation
    if (log.isDebugEnabled())
      log.debug("doAfterBody: register variable '"+name+"'.");

    putParameter(content, true);

    bodyContent.clearBody();
    return SKIP_BODY;
  }

  public int doEndTag() {
    resetMembers();
    return EVAL_PAGE;
  }

  // --- Attribute setters
  
  /**
   * Sets the name of the string.
   */
  public void setName(String s) {
    name = s;
  }
  
  /**
   * Sets the content of the string which can either be a path to a
   * file or the string itself, depending on the value of the "direct"
   * attribute.
   */
  public void setContent(String s) {
    content = s;
  }
  
  /**
   * Sets a flag which, if set to "true", will interpret the content
   * string directly.  If not, the tag expects a file path to be in
   * content. Default value is false.
   */
  public void setDirect(String s) {
    direct = s.equalsIgnoreCase("true");
  }
  
  /** 
   * Sets a flag which, if set to "true", will make the tag ignore the
   * "content" attribute and use the body content of the tag. Default
   * value is false.
   *
   * @deprecated attribute no longer neccessary to use becaue the
   * existence of a content attribute tells the tag whether to use the
   * body content or not.
   */
  public void setBody(String s) {
    // ignore
  }
  
  // --- Internal
  
  private void resetMembers() {
    // tag attributes
    name = null;
    content = null;
    direct = false;
  }
  
}
