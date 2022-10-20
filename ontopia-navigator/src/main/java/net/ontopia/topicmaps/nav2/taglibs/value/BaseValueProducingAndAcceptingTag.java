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

package net.ontopia.topicmaps.nav2.taglibs.value;

import java.util.Collection;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.ValueProducingTagIF;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/**
 * INTERNAL: Abstract super-class of a tag that is both value-producing
 * and value-accepting.
 *
 * Note: Not all value producing tags are manipulating collections,
 * so this is not the base class of all value producing tags.
 * Exceptions are StringTag and ClassesTag.
 */
public abstract class BaseValueProducingAndAcceptingTag extends BaseValueProducingTag
  implements ValueProducingTagIF, ValueAcceptingTagIF {

  // members
  protected ContextTag contextTag; // NOTE: only set inside doEndTag().
  protected Collection inputCollection;

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    // ignore body if variable name is set
    if (variableName != null) {
      return SKIP_BODY;
    } else {
      return EVAL_BODY_INCLUDE;
    }
  }

  /**
   * Process the end tag. Subclasses implementing this method must
   * clear member variables.
   */
  @Override
  public int doEndTag() throws JspException {
    // retrieve parent tag which accepts the produced collection by this tag 
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);
    
    // try to retrieve default value from ContextManager
    this.contextTag = FrameworkUtils.getContextTag(pageContext);    
    ContextManagerIF ctxtMgr = contextTag.getContextManager();

    // get collection on which we should compute 
    Collection inputCollection = getInputCollection(ctxtMgr);

    // collection processing
    Collection resultCollection = process( inputCollection );

    // kick result over to the accepting tag
    if (acceptingTag != null) {
      acceptingTag.accept( resultCollection );
    }

    // reset members
    this.contextTag = null;
    this.inputCollection = null;

    return EVAL_PAGE;
  }

  /**
   * INTERNAL: Return <code>inputCollection </code> if it was already
   * set, then call implementation from superclass.
   */  
  @Override
  protected Collection getInputCollection(ContextManagerIF ctxtMgr)
    throws NavigatorRuntimeException {
    // FIXME: This should perhaps instead depend in variableName != null.
    if (inputCollection != null) {
      return inputCollection;
    } else {
      return super.getInputCollection(ctxtMgr);
    }
  }
  
  // -----------------------------------------------------------------
  // Implementation of ValueAcceptingTagIF
  // -----------------------------------------------------------------

  @Override
  public void accept(Collection inputCollection) {
    this.inputCollection = inputCollection;
  }
  
}
