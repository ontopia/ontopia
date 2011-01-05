
package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.core.*;

/**
 * PUBLIC: A store factory that always returns the store given to it
 * via its constructor. This class is useful when the intention is
 * that the same store object is always to be used.</p>
 */
public class SameStoreFactory implements TopicMapStoreFactoryIF {

  protected TopicMapStoreIF store;

  /**
   * PUBLIC: Creates a TopicMapStoreFactoryIF which persistently
   * references the given store
   *
   * @param store the given topicMapStoreFactoryIF
   */ 
  public SameStoreFactory(TopicMapStoreIF store) {
    this.store = store;
  }

  /**
   * PUBLIC: Returns a topicmap store, which is the store given to the
   * constructor.
   *
   * @return The store received through the object's constructor.
   */
  public TopicMapStoreIF createStore() {
    return store;
  }    
  
}
