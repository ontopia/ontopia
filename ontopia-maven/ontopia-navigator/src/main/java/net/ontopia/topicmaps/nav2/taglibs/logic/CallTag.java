
// $Id: CallTag.java,v 1.23 2004/01/13 09:16:34 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.logic;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.BodyContent;
  
import net.ontopia.utils.ontojsp.*;

import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.core.*;
import net.ontopia.topicmaps.nav2.impl.basic.Function;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.utils.ontojsp.TaglibTagFactory;

import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Logic Tag for calling a template function and instantiates
 * its contents.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.FunctionTag
 */
public class CallTag extends TagSupport {

  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(CallTag.class.getName());
  
  // members
  private ContextTag contextTag;
  private ContextManagerIF ctxtMgr;
  
  // tag attributes
  private String functionName;
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspException {
    // get Context Tag
    contextTag = FrameworkUtils.getContextTag(pageContext);
    ctxtMgr = contextTag.getContextManager();

    // establish new lexical scope for this call
    ctxtMgr.pushScope();
    
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Process the end tag for this instance.
   */
  public int doEndTag() throws JspException {
    // retrieve function object from central managed pool
    FunctionIF function = contextTag.getFunction(functionName);
    if (function == null) {
      String msg = "CallTag: function with name '" + functionName +
        "' not found.";
      throw new NavigatorRuntimeException(msg);
    }

    // execute function
    if (log.isDebugEnabled()) {
      log.debug("Executing function:" + function.toString() +
                ", parent: " + getParent());
    }
    Collection retval = null;
    try {
      retval = function.execute(pageContext, this);
    } catch (Throwable e) {
      throw new NavigatorRuntimeException("Problems occurred while calling" +
                                          "function '" + functionName +
                                          "'.", e);
    }
    
    // establish old lexical scope, back to outside of the call
    ctxtMgr.popScope();

    // if return variable available push up to next acceptor
    if (retval != null) {
      // retrieve parent tag which accepts the result from function 
      ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
        findAncestorWithClass(this, ValueAcceptingTagIF.class);
      // kick it up to the accepting tag
      if (acceptingTag != null)
        acceptingTag.accept( retval );
      else
        log.info("No accepting tag found (function '" + functionName + "')");
    }

    // reset members
    contextTag = null;
    ctxtMgr = null;
    
    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }
  
  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  /**
   * INTERNAL: Sets the name of this function.
   */
  public void setName(String functionName) {
    this.functionName = functionName;
  }

}
