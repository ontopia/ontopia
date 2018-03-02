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
 * INTERNAL: Matches topics by their subject indicators.
 */
public class SubjectIndicatorMatcher implements TMObjectMatcherIF {
  protected LocatorIF locator;
  
  /**
   * INTERNAL: Creates a subject indicator matcher that matches topics
   * by the locator given.
   */
  public SubjectIndicatorMatcher(LocatorIF locator) {
    this.locator = locator;
  }

  /**
   * INTERNAL: Returns the locator used for matching.
   */
  public LocatorIF getLocator() {
    return locator;
  }

  // --- TMObjectMatcherIF methods
  
  @Override
  public boolean matches(TMObjectIF object) {
    if (!(object instanceof TopicIF))
      return false;

    TopicIF topic = (TopicIF) object;
    return topic != null && topic.getSubjectIdentifiers().contains(locator);
  }

  // --- Object methods
  
  @Override
  public String toString() {
    return "<SubjectIndicatorMatcher '" + locator + "'>";
  }

  @Override
  public boolean equals(TMObjectMatcherIF object) {
    if (object instanceof SubjectIndicatorMatcher)
      return ((SubjectIndicatorMatcher)object).getLocator() == this.getLocator();
    else return false;
  }

}
