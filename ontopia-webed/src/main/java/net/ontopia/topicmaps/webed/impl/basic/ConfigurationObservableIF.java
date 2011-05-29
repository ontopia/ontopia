
package net.ontopia.topicmaps.webed.impl.basic;

/**
 * INTERNAL: Classes implementing this interface may want to inform
 * the subscribed observers that a specific configuration event has
 * happend (like for example a new configuration element was
 * found).</p>
 */
public interface ConfigurationObservableIF {

  /**
   * Adds an observer to the set of observers for this object.
   */
  void addObserver(ConfigurationObserverIF o);

  /**
   * Removes an observer from the set of observers of this object.
   */
  void removeObserver(ConfigurationObserverIF o);
  
}
