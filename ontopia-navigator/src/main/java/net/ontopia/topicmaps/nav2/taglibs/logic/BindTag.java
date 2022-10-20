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

package net.ontopia.topicmaps.nav2.taglibs.logic;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Bind Tag for turning map keys into variables.
 */
public class BindTag extends TagSupport {

  // members
  private ContextManagerIF ctxtMgr;

  // tag attributes
  private String name;

  /**
   * Process the start tag.
   */
  @Override
  public int doStartTag() throws JspTagException {

    // retrieve collection from ContextManager by Name
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // Get the ContextManager
    this.ctxtMgr = contextTag.getContextManager();

    // establish new lexical scope for this condition...
    ctxtMgr.pushScope();

    // Get the Map
    Collection coll;
    if (name != null) {
      coll = ctxtMgr.getValue(name);
    } else {
      coll = ctxtMgr.getDefaultValue();
    }
    
    // We should only have one value in the collection
    if (coll.size() != 1) {
      throw new NavigatorRuntimeException("The collection passed on logic:bind" +
                                          " contains " + coll.size() + " entries");
    }

    // And it has to be a map
    Object value = CollectionUtils.getFirstElement(coll);
    if (!(value instanceof Map)) {
      throw new NavigatorRuntimeException("The value passed to logic:bind was not " +
                                          "a map, but " + value);
    }
    
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
  @Override
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
  @Override
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
