
package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A custom JSP tag which allows to create an attribute
 * name-value pair and assign it to the parent element tag, which is
 * responsible for the output.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.output.ElementTag 
 */
public class AttributeTag extends BodyTagSupport {
  
  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(AttributeTag.class.getName());

  // tag attributes
  private String attrName;

  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    return EVAL_BODY_BUFFERED;
  }

  /**
   * Actions after some body has been evaluated.
   */
  public int doAfterBody() throws JspTagException {
    ElementTag elementTag = (ElementTag)
      findAncestorWithClass(this, ElementTag.class);

    if (elementTag != null) {
      // get body content
      BodyContent body = getBodyContent();
      if (body != null) {
        String content = body.getString();
        // add this attribute to parent element tag 
        elementTag.addAttribute(attrName, content);
        body.clearBody();
      } else {
        log.warn("AttributeTag: body content is null!");
      }
    } else {
      log.warn("AttributeTag: no parent element tag found!");
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

  public final void setName(String attrName) {
    this.attrName = attrName;
  }

}





