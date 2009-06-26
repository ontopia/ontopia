
// $Id: BindTag.java,v 1.15 2004/03/25 14:23:14 larsga Exp $

package net.ontopia.topicmaps.nav2.taglibs.logic;

import java.util.*;
import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.utils.CollectionUtils;
import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.impl.basic.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.nav2.core.*;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.infoset.core.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * INTERNAL: Bind Tag for turning map keys into variables.
 */
public class BindTag extends TagSupport {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(BindTag.class.getName());

  // members
  private ContextManagerIF ctxtMgr;

  // tag attributes
  private String name;

  /**
   * Process the start tag.
   */
  public int doStartTag() throws JspTagException {

    // retrieve collection from ContextManager by Name
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // Get the ContextManager
    this.ctxtMgr = contextTag.getContextManager();

    // establish new lexical scope for this condition...
    ctxtMgr.pushScope();

    // Get the Map
    Collection coll;
    if (name != null)
      coll = ctxtMgr.getValue(name);
    else
      coll = ctxtMgr.getDefaultValue();
    
    // We should only have one value in the collection
    if (coll.size() != 1)
      throw new NavigatorRuntimeException("The collection passed on logic:bind" +
                                          " contains " + coll.size() + " entries");

    // And it has to be a map
    Object value = CollectionUtils.getFirstElement(coll);
    if (!(value instanceof Map))
      throw new NavigatorRuntimeException("The value passed to logic:bind was not " +
                                          "a map, but " + value);
    
    Map map = (Map) value;

    Iterator keys = map.keySet().iterator();
    while (keys.hasNext()) {
      String key = (String)keys.next();
      Object obj = map.get(key);
      ctxtMgr.setValue(key, obj);
    }
    // Evaluate the body.
    return EVAL_BODY_INCLUDE;
  }
 
  /**
   * Actions after the body.
   */
  public int doEndTag() throws JspTagException {
    // establish old lexical scope, back to outside of the condition
    ctxtMgr.popScope();

    // reset members
    ctxtMgr = null;
    
    return EVAL_PAGE;
  }

  /**
   * reset the state of the Tag.
   */
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }
  
  // -------------------------------------------------------
  // set methods for tag attributes
  // -------------------------------------------------------

  /**
   * Sets the name of which this bind belongs.
   */
  public void setName(String name) {
    this.name = name;
  }
}
