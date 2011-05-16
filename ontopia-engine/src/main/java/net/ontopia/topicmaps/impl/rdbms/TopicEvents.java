
// $Id: TopicEvents.java,v 1.4 2008/06/24 10:04:33 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.util.*;
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

  protected  RDBMSTopicMapStore store;
  
  protected Collection topicsAdded = new HashSet();
  protected Collection topicsModified = new HashSet(); 
  protected Map topicsRemoved = new HashMap();

  public TopicEvents(RDBMSTopicMapStore store) {
    this.store = store;
  }
  
  // -----------------------------------------------------------------------------
  // Transaction callbacks
  // -----------------------------------------------------------------------------
  
  void commitListeners() {
    if (store.topic_listeners != null) {
      TopicMapListenerIF[] topic_listeners = store.topic_listeners;
      Iterator aiter = topicsAdded.iterator();
      while (aiter.hasNext()) {
        TopicIF added = (TopicIF)aiter.next();
        for (int i=0; i < topic_listeners.length; i++) {
          try {
            topic_listeners[i].objectAdded(SnapshotTopic.makeSnapshot(added, SnapshotTMObject.SNAPSHOT_REFERENCE, new HashMap()));
          } catch (Exception e) {
            log.error("Exception was thrown from topic map listener " + topic_listeners[i], e);
          }
        }
      }
      Iterator miter = topicsModified.iterator();
      while (miter.hasNext()) {
        TopicIF modified = (TopicIF)miter.next();
        if (topicsAdded.contains(modified) || topicsRemoved.containsKey(modified)) continue;
        for (int i=0; i < topic_listeners.length; i++) {
          try {
            topic_listeners[i].objectModified(SnapshotTopic.makeSnapshot(modified, SnapshotTMObject.SNAPSHOT_REFERENCE, new HashMap()));
          } catch (Exception e) {
            log.error("Exception was thrown from topic map listener " + topic_listeners[i], e);
          }
        }
      }
      Iterator riter = topicsRemoved.keySet().iterator();
      while (riter.hasNext()) {
        TopicIF removed = (TopicIF)riter.next();
        if (!topicsAdded.contains(removed)) {
          for (int i=0; i < topic_listeners.length; i++) {
            try {
              topic_listeners[i].objectRemoved((TopicIF)topicsRemoved.get(removed));
            } catch (Exception e) {
              log.error("Exception was thrown from topic map listener " + topic_listeners[i], e);
            }
          }
        }
      }
      topicsAdded.clear();
      topicsModified.clear();
      topicsRemoved.clear();
    }
  }

  void abortListeners() {
    if (store.topic_listeners != null) {
      topicsAdded.clear();
      topicsRemoved.clear();
    }
  }
  
  // -----------------------------------------------------------------------------
  // Callbacks
  // -----------------------------------------------------------------------------
  
  // called when topic has just been added to the topic map
  void addedTopic(TopicIF topic) {
    if (store.topic_listeners != null) {
      topicsAdded.add(topic);
    }
  }

  // called when a topic is about to be removed from the topic map
  void removingTopic(TopicIF topic) {
    if (store.topic_listeners != null) {
      topicsAdded.remove(topic);
      if (!topicsRemoved.containsKey(topic))
        topicsRemoved.put(topic, SnapshotTopic.makeSnapshot(topic, SnapshotTMObject.SNAPSHOT_COMPLETE, new HashMap()));
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
    if (store.topic_listeners != null && "TopicIF.modified".equals(event))
      topicsModified.add(object);
  }
  
}
