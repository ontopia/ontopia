
package net.ontopia.topicmaps.schema.impl.osl;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
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
