/*
 * #!
 * Ontopia Navigator
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
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
  @Override
  public int doStartTag() throws JspTagException {
    attrnames.clear();
    attrs.clear();
    return EVAL_BODY_BUFFERED;
  }

  /**
   * Actions after some body has been evaluated.
   */
  @Override
  public int doAfterBody() throws JspTagException {
    BodyContent body = getBodyContent();
    StringBuilder complElem = new StringBuilder(100);
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
  @Override
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
    if (!attrnames.contains(name)) {
      attrnames.add(name);
    }
    attrs.put(name, value);
  }

}





