// $Id: EventListenerIF.java,v 1.3 2002/05/29 13:38:39 hca Exp $

package net.ontopia.topicmaps.impl.utils;

/**
 * INTERNAL: An event listener interface.
 */

public interface EventListenerIF {

  /**
   * INTERNAL: A method that receives notification when an event has been triggered.
   */
  public void processEvent(Object object, String event, Object new_value, Object old_value);
  
}





