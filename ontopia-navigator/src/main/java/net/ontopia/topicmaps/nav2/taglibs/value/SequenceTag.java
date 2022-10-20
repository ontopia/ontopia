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

import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;

/**
 * INTERNAL: Tag which gathers all the values it receives into a
 * sequence. All the collections received are flattened into a single
 * sequence.
 */
public class SequenceTag extends TagSupport implements ValueAcceptingTagIF {
  // members
  private Collection resultCollection;

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() {
    // evaluate body
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Process the end tag.
   */
  @Override
  public int doEndTag() {
    // retrieve parent tag which accepts the collection produced by this tag 
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);

    // kick it over to the accepting tag
    acceptingTag.accept( resultCollection );

    // reset member collection
    resultCollection = null;

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

  // -----------------------------------------------------------------
  // Implementation of ValueAcceptingTagIF
  // -----------------------------------------------------------------

  @Override
  public void accept(Collection inputCollection) {
    if (resultCollection == null) {
      resultCollection = new ArrayList();
    }

    // we accept duplicates, and the list will ensure that we respect order
    resultCollection.addAll( inputCollection );
  }
}
