
// $Id: InMemoryStoreFactory.java,v 1.6 2005/07/12 08:58:37 grove Exp $

package net.ontopia.topicmaps.impl.basic;

import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

/**
 * PUBLIC: The in-memory TopicMapStoreFactoryIF implementation.
 */

public class InMemoryStoreFactory implements TopicMapStoreFactoryIF {
  
  public TopicMapStoreIF createStore() {
    return new InMemoryTopicMapStore();
  }
  
}
