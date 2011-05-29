
package net.ontopia.topicmaps.schema.impl.osl;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;

/**
 * INTERNAL: An object matcher that matches any object. Useful mainly
 * as a wildcard.
 */
public class AnyTopicMatcher implements TMObjectMatcherIF {

  public boolean matches(TMObjectIF topic) {
    return true;
  }

  public String toString() {
    return "<AnyTopicMatcher>";
  }

  public boolean equals(TMObjectMatcherIF object) {
    return false;
  }
}






