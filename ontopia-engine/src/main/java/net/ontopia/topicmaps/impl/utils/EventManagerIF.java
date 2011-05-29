
package net.ontopia.topicmaps.impl.utils;

/**
 * INTERNAL: An event manager listener interface.
 */

public interface EventManagerIF extends EventListenerIF {

  /**
   * INTERNAL: Register the listener as a listener for the event.
   */
  public void addListener(EventListenerIF listener, String event);

  /**
   * INTERNAL: Unregister the listener as a listener for the event.
   */
  public void removeListener(EventListenerIF listener, String event);

}





