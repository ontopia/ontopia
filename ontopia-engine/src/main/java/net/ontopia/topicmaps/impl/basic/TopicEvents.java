/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.impl.basic;

import java.util.HashMap;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.impl.utils.EventListenerIF;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.SnapshotTMObject;
import net.ontopia.topicmaps.impl.utils.SnapshotTopic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Internal event listener class that handles topic events.<p>
 */

public class TopicEvents implements EventListenerIF {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(TopicEvents.class.getName());

  protected  InMemoryTopicMapStore store;

  public TopicEvents(InMemoryTopicMapStore store) {
    this.store = store;
  }
  
  // -----------------------------------------------------------------------------
  // Callbacks
  // -----------------------------------------------------------------------------

  // called when topic has just been added to the topic map
  protected void addedTopic(TopicIF topic) {
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
  protected void removingTopic(TopicIF topic) {
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

  protected void registerListeners(EventManagerIF emanager) {
    // listen to topic modification events
    emanager.addListener(this, TopicIF.EVENT_MODIFIED);
  }
  
  public void processEvent(Object object, String event, Object new_value, Object old_value) {
    if (TopicIF.EVENT_MODIFIED.equals(event)) {
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
