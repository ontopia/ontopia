package net.ontopia.topicmaps;

import net.ontopia.AbstractOntopiaTestCase;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;

public abstract class AbstractTopicMapTestCase extends AbstractOntopiaTestCase {

  public AbstractTopicMapTestCase(String name) {
    super(name);
  }

  public TopicIF getTopicById(TopicMapIF topicmap, String id) {
    LocatorIF base = topicmap.getStore().getBaseAddress();
    return (TopicIF)
    topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }

  protected TMObjectIF getObjectById(TopicMapIF topicmap, String id) {
    LocatorIF base = topicmap.getStore().getBaseAddress();
    return (TMObjectIF)
    topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }

  public TopicIF getTopicById(TopicMapIF topicmap, LocatorIF base, String id) {
    return (TopicIF)
    topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }

  public TMObjectIF getObjectById(TopicMapIF topicmap, LocatorIF base, String id) {
    return topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }

}
