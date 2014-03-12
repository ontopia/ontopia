/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

package net.ontopia.topicmaps.schema.impl.osl;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;

/**
 * INTERNAL: A topic matcher that matches topics by their source
 * locators.  It uses a string that is evaluated relative to the
 * base URI of the topic map.
 */
public class InternalTopicRefMatcher implements TMObjectMatcherIF {
  protected String relativeUri;
  
  /**
   * INTERNAL: Creates a new InternalTopicRefMatcher.
   * @param relativeUri The URI used for matching.
   */
  public InternalTopicRefMatcher(String relativeUri) {
    this.relativeUri = relativeUri;
  }

  /**
   * INTERNAL: Returns the relative URI which will be used for matching.
   * It will be evaluated relative to the base URI of the topic map to
   * which the topic being matched belongs.
   */
  public String getRelativeURI() {
    return relativeUri;
  }

  // --- TMObjectMatcherIF methods
  
  public boolean matches(TMObjectIF object) {
    if (object == null)
      return false;

    if (!(object instanceof TopicIF))
      return false;

    TopicIF topic = (TopicIF) object;
    LocatorIF resolved = topic.getTopicMap().getStore().getBaseAddress().resolveAbsolute(relativeUri);
    return topic.getItemIdentifiers().contains(resolved);
  }

  public String toString() {
    return "<InternalTopicRefMatcher '" + relativeUri + "'>";
  }

  public boolean equals(TMObjectMatcherIF object) {
    if (object instanceof InternalTopicRefMatcher)
      return this.getRelativeURI().equals(((InternalTopicRefMatcher)object).getRelativeURI());
    else return false;
  }
}
