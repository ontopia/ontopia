
// $Id: TopicEvents.java,v 1.4 2008/06/24 10:04:33 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.basic;

import java.util.*;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.events.*;
import net.ontopia.topicmaps.impl.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Internal event listener class that handles topic events.<p>
 */

public class TopicEvents implements EventListenerIF {
  
  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(TopicEvents.class.getName());

  protected  InMemoryTopicMapStore store;

  public TopicEvents(InMemoryTopicMapStore store) {
    this.store = store;
  }
  
  // -----------------------------------------------------------------------------
  // Callbacks
  // -----------------------------------------------------------------------------

  // called when topic has just been added to the topic map
  void addedTopic(TopicIF topic) {
    if (store.topic_listeners != null) {
      synchronized (this) {
        TopicMapListenerIF[] topic_listeners = store.topic_listeners;
        for (int i=0; i < topic_listeners.length; i++) {
          try {
            topic_listeners[i].objectAdded(SnapshotTopic.makeSnapshot(topic, SnapshotTMObject.SNAPSHOT_REFERENCE, new HashMap<TMObjectIF, SnapshotTMObject>()));
          } catch (Exception e) {
            log.error("Exception was thrown from topic map listener " + topic_listeners[i], e);
          }
        }
      }
    }
  }

  // called when a topic is about to be removed from the topic map
  void removingTopic(TopicIF topic) {
    if (store.topic_listeners != null) {
      synchronized (this) {
        TopicMapListenerIF[] topic_listeners = store.topic_listeners;
        for (int i=0; i < topic_listeners.length; i++) {
          try {
            topic_listeners[i].objectRemoved(SnapshotTopic.makeSnapshot(topic, SnapshotTMObject.SNAPSHOT_COMPLETE, new HashMap<TMObjectIF, SnapshotTMObject>()));
          } catch (Exception e) {
            log.error("Exception was thrown from topic map listener " + topic_listeners[i], e);
          }
        }
      }
    }
  }
  
  // -----------------------------------------------------------------------------
  // EventListenerIF
  // -----------------------------------------------------------------------------

  void registerListeners(EventManagerIF emanager) {
    // listen to topic modification events
    emanager.addListener(this, "TopicIF.modified");
  }
  
  public void processEvent(Object object, String event, Object new_value, Object old_value) {
    if ("TopicIF.modified".equals(event)) {
      TopicIF topic = (TopicIF)object;
      synchronized (this) {
        TopicMapListenerIF[] topic_listeners = store.topic_listeners;
        if (topic_listeners != null) {
          for (int i=0; i < topic_listeners.length; i++) {
            try {
              topic_listeners[i].objectModified(SnapshotTopic.makeSnapshot(topic, SnapshotTMObject.SNAPSHOT_REFERENCE, new HashMap<TMObjectIF, SnapshotTMObject>()));
            } catch (Exception e) {
              log.error("Exception was thrown from topic map listener " + topic_listeners[i], e);
            }
          }
        }
      }
    }
  }
  
}
