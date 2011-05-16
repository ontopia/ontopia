
package net.ontopia.topicmaps.entry;

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.utils.SameStoreFactory;

/**
 * INTERNAL: A topic map reference that wraps a TopicMapStoreIF
 * object. The reference always returns the same store instance.<p>
 *
 * If the reference has been opened, ie. used to create stores, and
 * the dereferenceOnClose flag is true, it cannot be reopened. If the
 * flag is false, it can be reopened if the store can be reopened.<p>
 *
 * @deprecated Class is now superseded by StoreFactoryReference.
 */

public class DefaultTopicMapReference extends StoreFactoryReference {
  
  public DefaultTopicMapReference(String id, String title, 
                                  TopicMapStoreIF store) {
    super(id, title, new SameStoreFactory(store));
  }
  
}
