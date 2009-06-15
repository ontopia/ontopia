
// $Id: LinkTag.java,v 1.42 2007/07/25 07:46:56 eirik.opland Exp $

package net.ontopia.topicmaps.webed.taglibs.form;

import java.util.Collections;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.ontopia.topicmaps.webed.impl.utils.TagUtils;
import net.ontopia.utils.OntopiaRuntimeException;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

/**
 * INTERNAL: A link tag that when clicked submits the form that it's in and
 * goes to the href given by the href attribute.
 */
public class LinkTag extends BodyTagSupport {
  private static final String CATEGORY_NAME = LinkTag.class.getName();
  private static Logger log = Logger.getLogger(LinkTag.class.getName());
  
  /**
   * The location where the velocity template can be retrieved from.
   */
  protected final static String TEMPLATE_FILE = "link.vm";

  // --- Tag Attributes

  protected String readonly;
  protected String klass;
  protected String action_name;
  protected String params;
  protected String href;
  protected String target;
  protected String title;
  protected String type;

  /**
   * Process the start tag, do nothing.
   * @return <code>EVAL_BODY_INCLUDE</code>
   */
  public int doStartTag() {
    return EVAL_BODY_BUFFERED;
  }
  
  /**
   * Generate the required input tag.
   * @exception JspException if a JSP exception has occurred
   */
  public int doEndTag() throws JspException {
    // Get the velocity context.
    VelocityContext vc = TagUtils.getVelocityContext(pageContext);

    FormTag form = TagUtils.getCurrentFormTag(pageContext.getRequest());

    boolean readonly = TagUtils.isComponentReadOnly(pageContext, this.readonly);
    
    if (!readonly) {
      // Get the requestid of the ancestor form of this tag.
      String rid = TagUtils.getRequestId(pageContext);
      if (rid == null)
        throw new OntopiaRuntimeException("Request id not found.");
      vc.put("rid", rid);
      if (form != null)
        form.setOutputSubmitFunc(true);
    }
    
    
    if (action_name != null && !readonly) {
      // retrieve the action group
      String group_name = TagUtils.getActionGroup(pageContext);
      if (group_name == null)
        throw new JspException("link tag has no action group available.");
    
      // register action data    
      Set previous = Collections.singleton(""); 
      String name = TagUtils.registerData(pageContext, action_name, group_name,
                                          params, previous);
      vc.put("name", name);
    } else {
      vc.put("name", "");
    }

    // read-only state
    vc.put("readonly", new Boolean(readonly));
        
    if (href != null)
      vc.put("href", href);
    else
      vc.put("href", "");

    if (target != null)
      vc.put("target", target);
    else
      vc.put("target", "");

    // submit and reload information
    if (type == null || type.startsWith("submit")) {
      vc.put("submit", Boolean.TRUE);
      if (type != null && type.equals("submitNoReload"))
        vc.put("reload", Boolean.FALSE);
      else
        vc.put("reload", Boolean.TRUE);        
    } else {
      vc.put("submit", Boolean.FALSE);
    }
    
    if (id != null) vc.put("id", id);
    if (klass != null) vc.put("class", klass);
    
    // get link value from element content.
    BodyContent bodyContent = getBodyContent();
    String content = (bodyContent == null ? "" : bodyContent.getString());
    if (content == null) content = "";
    vc.put("content", content);
    
    // all variables are now set, proceed wi2th outputting
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
    href = null;
    target = null;
    title = null;
    type = null;
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
   * Sets the url that the link should point to.
   */
  public void setHref(String href) {
    this.href = href;
  }

  /**
   * Sets the target frame that the link should be opened in.
   */
  public void setTarget(String target) {
    this.target = target;
  }

  /**
   * Sets the title to be used to display the link.
   */
  public void setTitle(String href) {
    this.href = href;
  }

  /**
   * Sets the link type (submit|submitnoreload|nosubmit).
   */
  public void setType(String type) {
    this.type = type;
  }

}
