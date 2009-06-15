// $Id: ElementTag.java,v 1.11 2007/01/08 07:42:37 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

/**
 * INTERNAL: A custom JSP tag which allows to output an element
 * (ie. HTML) with dynamic generated attribute values (ie. by other
 * custom tags) and content.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.output.AttributeTag
 */
public class ElementTag extends BodyTagSupport {

  // members
  protected List attrnames = new ArrayList();
  protected HashMap attrs = new HashMap();
  
  // tag attributes
  protected String elemName;

  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    attrnames.clear();
    attrs.clear();
    return EVAL_BODY_BUFFERED;
  }

  /**
   * Actions after some body has been evaluated.
   */
  public int doAfterBody() throws JspTagException {
    BodyContent body = getBodyContent();
    StringBuffer complElem = new StringBuffer(100);
    complElem.append("<").append(elemName);
    // put in attribute-value pairs
    Iterator it = attrnames.iterator();
    String key, val;    
    while (it.hasNext()) {
      key = (String) it.next();
      val = (String) attrs.get(key);
      complElem.append(" ").append(key).append("='").append(val).append("'");
    }
    complElem.append(">");

    // append body content
    complElem.append( body.getString() );
    complElem.append("</").append(elemName).append(">");
    
    // write it out
    try {
      JspWriter out = body.getEnclosingWriter();
      out.print( complElem.toString() );
      body.clearBody();
    } catch (IOException ioe) {
      throw new NavigatorRuntimeException("Error in ElementTag. JspWriter not there.", ioe);
    }
    
    // only evaluate body once
    return SKIP_BODY;
  }
  

  /**
   * reset the state of the Tag.
   */
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }
  
  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  public final void setName(String elemName) {
    this.elemName = elemName;
  }
  
  // -----------------------------------------------------------------
  // methods for inter-tag communication
  // -----------------------------------------------------------------

  public void addAttribute(String name, String value) {
    if (!attrnames.contains(name)) attrnames.add(name);
    attrs.put(name, value);
  }

}





