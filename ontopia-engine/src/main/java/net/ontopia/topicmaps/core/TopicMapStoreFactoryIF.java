
package net.ontopia.topicmaps.core;


/**
 * PUBLIC: Factory interface for creating new topic map store
 * objects.</p>
 *
 * Factories are used to make object creation independent of the
 * specific topic map implementation used. </p>
 */

public interface TopicMapStoreFactoryIF {

  /**
   * PUBLIC: Creates a topic map store object.
   *
   * @return An object implementing TopicMapStoreIF
   */
  public TopicMapStoreIF createStore();
  
}
