/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.schema.impl.osl;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;

/**
 * INTERNAL: Object matcher that matches objects by their source locators.
 */
public class SourceLocatorMatcher implements TMObjectMatcherIF {
  protected LocatorIF locator;
  
  /**
   * INTERNAL: Creates a new matcher with the locator it uses to match.
   */
  public SourceLocatorMatcher(LocatorIF locator) {
    this.locator = locator;
  }

  /**
   * INTERNAL: Returns the locator used for matching.
   */
  public LocatorIF getLocator() {
    return locator;
  }

  // --- TMObjectMatcherIF methods
  
  public boolean matches(TMObjectIF object) {
    return object != null && object.getItemIdentifiers().contains(locator);
  }

  // --- Object methods
  
  public String toString() {
    return "<SourceLocatorMatcher '" + locator + "'>";
  }

  public boolean equals(TMObjectMatcherIF object) {
    return false;
  }
  
}






