
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
