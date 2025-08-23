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

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.Collection;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Value Producing Tag for generating a collection of
 * TopicMapReferenceIF objects containing information about topicmaps
 * available to this application.
 * <p>
 * (Note: this is somewhat special because this Tag does not need to
 *  manipulate an input collection and so it is not implementing
 * the interface ValueProducingTagIF).
 */
public class TopicMapReferencesTag extends TagSupport {

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    // retrieve parent tag which accepts the produced collection by this tag 
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);

    // try to retrieve root ContextTag
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // get collection of TM Reference entries from Configuration
    Collection refs =
      contextTag.getNavigatorApplication().getTopicMapRepository().getReferences();
    
    // kick it up to the accepting tag
    acceptingTag.accept( refs );

    // ignore body, because this is an empty tag
    return SKIP_BODY;
  }
  
}





