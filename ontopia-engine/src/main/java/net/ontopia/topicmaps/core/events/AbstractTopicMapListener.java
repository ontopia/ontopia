
// $Id: AbstractTopicMapListener.java,v 1.4 2007/10/17 08:18:06 geir.gronmo Exp $

package net.ontopia.topicmaps.core.events;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

/**
 * PUBLIC: Base class for topic map listeners. Subclass this abstract
 * listener class if you want to create your own listener
 * implementation. Subclassing this class will make sure that any
 * extensions to the TopicMapListenerIF interface will be catered for
 * in the future, preventing inconsistencies. Methods implemented by
 * this abstract class have empty method bodies.
 *
 * @since 3.4.3
 */

public abstract class AbstractTopicMapListener implements TopicMapListenerIF {
  
  public void objectAdded(TMObjectIF snapshot) {
    // no-op
  }

  public void objectModified(TMObjectIF snapshot) {
    // no-op
  }

  public void objectRemoved(TMObjectIF snapshot) {
    // no-op 
  }

  /**
   * INTERNAL: Callback method called when listener is being
   * registered or unregistered with a topic map reference.
   */
  public void setReference(TopicMapReferenceIF ref) {
    // no-op
  }
  
}
