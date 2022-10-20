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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
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
  private static final Logger log = LoggerFactory
    .getLogger(CallTag.class.getName());
  
  // members
  private ContextTag contextTag;
  private ContextManagerIF ctxtMgr;
  
  // tag attributes
  private String functionName;
  
  /**
   * Process the start tag for this instance.
   */
  @Override
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
  @Override
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
      if (acceptingTag != null) {
        acceptingTag.accept( retval );
      } else {
        log.info("No accepting tag found (function '" + functionName + "')");
      }
    }

    // reset members
    contextTag = null;
    ctxtMgr = null;
    
    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  @Override
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
