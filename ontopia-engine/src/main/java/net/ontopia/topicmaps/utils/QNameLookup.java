
package net.ontopia.topicmaps.utils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * PUBLIC: A utility class for producing full URIs from QNames. Allows
 * QName prefixes to be registered, and has a set of predefined QName
 * prefixes. Also allows topics to be looked up, via the QNameLookup
 * class.
 * @since 5.0.0
 */
public class QNameLookup {
  private QNameRegistry registry;
  private TopicMapIF topicmap;

  QNameLookup(QNameRegistry registry, TopicMapIF topicmap) {
    this.registry = registry;
    this.topicmap = topicmap;
  }

  public TopicIF lookup(String qname) {
    LocatorIF si = registry.resolve(qname);
    return topicmap.getTopicBySubjectIdentifier(si);
  }
}
