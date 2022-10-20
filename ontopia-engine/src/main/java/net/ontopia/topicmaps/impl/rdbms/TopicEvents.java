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

package net.ontopia.topicmaps.impl.rdbms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.impl.utils.EventListenerIF;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.SnapshotTMObject;
import net.ontopia.topicmaps.impl.utils.SnapshotTopic;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Internal event listener class that handles topic events.<p>
 */
public class TopicEvents implements EventListenerIF {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(TopicEvents.class.getName());

  protected  RDBMSTopicMapStore store;
  
  protected Set<TopicIF> topicsAdded = new HashSet<TopicIF>();
  protected Set<TopicIF> topicsModified = new HashSet<TopicIF>(); 
  protected Map<TopicIF, TopicIF> topicsRemoved = new HashMap<TopicIF, TopicIF>();

  public TopicEvents(RDBMSTopicMapStore store) {
    this.store = store;
  }
  
  // -----------------------------------------------------------------------------
  // Transaction callbacks
  // -----------------------------------------------------------------------------
  
  protected void commitListeners() {
    if (store.topic_listeners != null) {
      TopicMapListenerIF[] topic_listeners = store.topic_listeners;
      for (TopicIF added : topicsAdded) {
        for (int i=0; i < topic_listeners.length; i++) {
          try {
            topic_listeners[i].objectAdded(SnapshotTopic.makeSnapshot(added, SnapshotTMObject.SNAPSHOT_REFERENCE, new HashMap<TMObjectIF, SnapshotTMObject>()));
          } catch (Exception e) {
            log.error("Exception was thrown from topic map listener " + topic_listeners[i], e);
          }
        }
      }
      for (TopicIF modified : topicsModified) {
        if (topicsAdded.contains(modified) || topicsRemoved.containsKey(modified)) {
          continue;
        }
        for (int i=0; i < topic_listeners.length; i++) {
          try {
            topic_listeners[i].objectModified(SnapshotTopic.makeSnapshot(modified, SnapshotTMObject.SNAPSHOT_REFERENCE, new HashMap<TMObjectIF, SnapshotTMObject>()));
          } catch (Exception e) {
            log.error("Exception was thrown from topic map listener " + topic_listeners[i], e);
          }
        }
      }
      for (TopicIF removed : topicsRemoved.keySet()) {
        if (!topicsAdded.contains(removed)) {
          for (int i=0; i < topic_listeners.length; i++) {
            try {
              topic_listeners[i].objectRemoved(topicsRemoved.get(removed));
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

  protected void abortListeners() {
    if (store.topic_listeners != null) {
      topicsAdded.clear();
      topicsRemoved.clear();
    }
  }
  
  // -----------------------------------------------------------------------------
  // Callbacks
  // -----------------------------------------------------------------------------
  
  // called when topic has just been added to the topic map
  protected void addedTopic(TopicIF topic) {
    if (store.topic_listeners != null) {
      topicsAdded.add(topic);
    }
  }

  // called when a topic is about to be removed from the topic map
  protected void removingTopic(TopicIF topic) {
    if (store.topic_listeners != null) {
      topicsAdded.remove(topic);
      if (!topicsRemoved.containsKey(topic)) {
        topicsRemoved.put(topic, SnapshotTopic.makeSnapshot(topic, SnapshotTMObject.SNAPSHOT_COMPLETE, new HashMap<TMObjectIF, SnapshotTMObject>()));
      }
    }
  }
  
  // -----------------------------------------------------------------------------
  // EventListenerIF
  // -----------------------------------------------------------------------------

  protected void registerListeners(EventManagerIF emanager) {
    // listen to topic modification events
    emanager.addListener(this, TopicIF.EVENT_MODIFIED);
    emanager.addListener(this, TopicMapTransactionIF.EVENT_COMMIT);
    emanager.addListener(this, TopicMapTransactionIF.EVENT_ABORT);
  }
  
  @Override
  public void processEvent(Object object, String event, Object new_value, Object old_value) {
    if (store.topic_listeners != null && TopicIF.EVENT_MODIFIED.equals(event)) {
      topicsModified.add((TopicIF)object);
    }
    if (TopicMapTransactionIF.EVENT_COMMIT.equals(event)) {
      commitListeners();
    }
    if (TopicMapTransactionIF.EVENT_ABORT.equals(event)) {
      abortListeners();
    }
  }
  
}
