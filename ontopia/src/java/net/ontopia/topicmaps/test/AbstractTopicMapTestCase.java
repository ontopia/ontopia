
// $Id: AbstractTopicMapTestCase.java,v 1.14 2008/06/13 08:36:28 geir.gronmo Exp $

package net.ontopia.topicmaps.test;

import java.io.*;
import net.ontopia.test.*;
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
