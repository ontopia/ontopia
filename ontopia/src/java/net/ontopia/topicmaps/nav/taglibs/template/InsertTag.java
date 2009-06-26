
// $Id: InsertTag.java,v 1.23 2006/04/20 10:53:59 grove Exp $

package net.ontopia.topicmaps.nav.taglibs.template;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Stack;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.utils.StringUtils;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Defines the template JSP to forward the request to, once
 * all of the PutTag strings have been stored.<p>
 * 
 * <h3>Example</h3>
 * <code>&lt;template:insert template='/views/template.jsp'&gt;</code>
 */
public class InsertTag extends TagSupport {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(InsertTag.class.getName());

  
  public final static String TEMPL_STACK_KEY = "template-stack";
  public final static String VIEW_PLACEHOLDER = "%view%";
  
  private String template;
  private Stack stack;

  public int doStartTag() throws JspException {
    stack = getStack();
    stack.push(new HashMap());
    
    return EVAL_BODY_INCLUDE;
  }
  
  public int doEndTag() throws JspException {
    try {
      if (log.isDebugEnabled())
        log.debug("doEndTag, template: '" + template + "'.");
      pageContext.include(template);
    }
    catch(java.io.IOException ex) {
      throw new NavigatorRuntimeException("InsertTag: I/O Error while including " +
                                          "template '" + template + "'", ex);
    }
    catch(javax.servlet.ServletException ex) {
      throw new NavigatorRuntimeException("InsertTag: while including " +
                                          "template '" + template + "'", ex);
    }
    stack.pop();

    releaseMembers();
    return EVAL_PAGE;
  }
  
  private void releaseMembers() {
    // members
    stack = null;
    // tag attributes
    template = null;
  }
  
  public Stack getStack() {
    Stack s = (Stack) pageContext.getAttribute(TEMPL_STACK_KEY,
                                               PageContext.REQUEST_SCOPE);
    if (s == null) {
      s = new Stack();
      pageContext.setAttribute(TEMPL_STACK_KEY, s,
                               PageContext.REQUEST_SCOPE);
    }
    return s;
  }

  /**
   * Sets (according to attribute 'template') a path to the template
   * page.  <br><p>
   *
   * Note: You can use a special placeholder <code>%view%</code> if
   * you want to insert the name of the current view of the user
   * session.  This is a work-around, because JSP does not allow you
   * to use a custom tag inside another custom tag
   */
  public void setTemplate(String templateString) {
    template = templateString;
    
    // special extension for the needs of the MVS support
    // replace view placeholder with current view value
    if (template.indexOf(VIEW_PLACEHOLDER) >= 0) {
      UserIF user = FrameworkUtils.getUser(pageContext);
      String view = user.getView();
      template = StringUtils.replace(template, VIEW_PLACEHOLDER, view);
    }
  }
  
}
