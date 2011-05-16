
// $Id: SubjectIndicatorMatcher.java,v 1.9 2008/06/13 08:17:54 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
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
  
  public boolean matches(TMObjectIF object) {
    if (!(object instanceof TopicIF))
      return false;

    TopicIF topic = (TopicIF) object;
    return topic != null && topic.getSubjectIdentifiers().contains(locator);
  }

  // --- Object methods
  
  public String toString() {
    return "<SubjectIndicatorMatcher '" + locator + "'>";
  }

  public boolean equals(TMObjectMatcherIF object) {
    if (object instanceof SubjectIndicatorMatcher)
      return ((SubjectIndicatorMatcher)object).getLocator() == this.getLocator();
    else return false;
  }

}
