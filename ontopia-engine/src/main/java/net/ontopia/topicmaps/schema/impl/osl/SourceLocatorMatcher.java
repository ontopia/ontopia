
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






