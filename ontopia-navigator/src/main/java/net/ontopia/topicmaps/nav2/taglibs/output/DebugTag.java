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
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.OutputProducingTagIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Output-producing tag used for debugging. It calls
 * toString() on the collection to show unambiguously what it
 * contains.
 *
 * @since 2.0
 */
public class DebugTag extends TagSupport
  implements OutputProducingTagIF {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(DebugTag.class.getName());

  // members
  protected ContextTag contextTag;  
  
  // tag attributes
  protected String variableName;

  public DebugTag() {
  }

  /**
   * INTERNAL: Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    // retrieve collection from ContextManager by Name
    this.contextTag = FrameworkUtils.getContextTag(pageContext);
    ContextManagerIF ctxtMgr = contextTag.getContextManager();
    
    Collection coll;
    if (variableName != null) 
      coll = ctxtMgr.getValue(variableName);
    else
      coll = ctxtMgr.getDefaultValue();

    try {
      JspWriter out = pageContext.getOut();    
      out.print(StringUtils.escapeHTMLEntities(coll.toString()));
    } catch (IOException e) {
      throw new NavigatorRuntimeException(e); // JSP sucks
    }

    // reset members
    contextTag = null;

    return SKIP_BODY;
  }

  public final int doEndTag() {
    // reset members
    this.contextTag = null;
    
    return EVAL_PAGE;
  }
  
  /**
   * INTERNAL: Reset the state of the Tag.
   */
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }

  // have to define this to keep the compiler happy... :(
  public void generateOutput(JspWriter out, Iterator iterator)
    throws JspTagException, IOException {
  }
  
  // -----------------------------------------------------------------
  // set methods
  // -----------------------------------------------------------------

  /**
   * INTERNAL: Tag attribute for setting the variable name of the
   * input collection.
   */
  public final void setOf(String variableName) {
    this.variableName = variableName;
  }  
}
