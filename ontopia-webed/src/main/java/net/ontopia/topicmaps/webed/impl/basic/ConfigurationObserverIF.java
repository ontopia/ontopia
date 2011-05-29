
package net.ontopia.topicmaps.webed.impl.basic;

/**
 * INTERNAL: A class implementing this interface most likely wants to
 * react on the observed event.
 */
public interface ConfigurationObserverIF {

  /**
   * Called when the observed configuration has changed in some matter.
   */
  void configurationChanged(Object configuration);
  
}
