
// $Id: OtherwiseTag.java,v 1.5 2005/03/30 17:36:36 opland Exp $

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Like a WhenTag with no condition. 
 * Evaluates its body if no earlier WhenTag was executed within
 * the parent ChooseTag.
 */
public class OtherwiseTag extends BodyTagSupport {

  // initialization of logging facility
  private static Logger log = Logger.getLogger(WhenTag.class.getName());

  // members
  protected ChooseTag parentChooser;
  
  /**
   * Default constructor.
   */
  public OtherwiseTag() {
    super();
  }
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    parentChooser = (ChooseTag) findAncestorWithClass(this, ChooseTag.class);
    if (parentChooser == null)
      throw new JspTagException(
              "tolog:otherwise tag is not inside tolog:choose tag.");

    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    
    if (contextTag == null)
      throw new JspTagException("<tolog:otherwise> must be nested directly or"
              + " indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> tag was found.");
    
    contextTag.getContextManager().pushScope();
    
    // If a matching when was already found within the parentChooser
    if (parentChooser.foundMatchingWhen())
      // No more WhenTags need to be executed (tested in each WhenTag).
      return SKIP_BODY;
    return EVAL_BODY_BUFFERED;
  }

  /** 
   * Actions after some body has been evaluated.
   */
  public int doAfterBody() throws JspTagException {
    // put out the evaluated body
    BodyContent body = getBodyContent();
    JspWriter out = body.getEnclosingWriter();
    try {
      out.print( body.getString() );
    } catch(IOException ioe) {
      throw new NavigatorRuntimeException("Error in IfTag.", ioe);
    }

    parentChooser.setFoundMatchingWhen();
    
    return SKIP_BODY;
  }
  
  /**
    * Process the end tag.
    */
  public int doEndTag() throws JspException {
    // establish old lexical scope, back to outside of the loop
    FrameworkUtils.getContextTag(pageContext).getContextManager().popScope();

    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  public void release() {
    // reset members
    parentChooser = null;
  }
}
