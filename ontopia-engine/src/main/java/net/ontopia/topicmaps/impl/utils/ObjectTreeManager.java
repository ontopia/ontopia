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

package net.ontopia.topicmaps.impl.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.CollectionFactoryIF;

/**
 * INTERNAL: Event manager that fires object added and removed
 * events for children of added or removed objects.</p>
 *
 * This class is itself an event listener that listens to the standard
 * property change events fired by the object model. Objects that are
 * added or removed are traversed and further events for the children
 * of those objects are fired.</p>
 *
 * If your event listeners depends on knowing when objects are added
 * or removed use this class instead of doing the traversal
 * yourself.</p>
 */

public class ObjectTreeManager implements EventManagerIF, java.io.Serializable {

  protected Map<String, EventHandler> handlers; 
  protected Map<String, Set<EventListenerIF>> listeners;

  protected ObjectTreeManager otree;
  protected CollectionFactoryIF cfactory;
  
  public ObjectTreeManager(EventManagerIF emanager, CollectionFactoryIF cfactory) {
    // Setup 
    this.cfactory = cfactory;

    // Initialize listeners
    listeners = cfactory.makeLargeMap();
    
    // Initialize handlers map
    handlers = cfactory.makeLargeMap();

    // Initialize parent-child relationship event handlers
    handlers.put(TopicMapIF.EVENT_ADD_TOPIC, new EH01());
    handlers.put(TopicMapIF.EVENT_REMOVE_TOPIC, new EH02());
    handlers.put(TopicMapIF.EVENT_ADD_ASSOCIATION, new EH03());
    handlers.put(TopicMapIF.EVENT_REMOVE_ASSOCIATION, new EH04());
    handlers.put(TopicIF.EVENT_ADD_TOPICNAME, new EH07());
    handlers.put(TopicIF.EVENT_REMOVE_TOPICNAME, new EH08());
    handlers.put(TopicNameIF.EVENT_ADD_VARIANT, new EH09());
    handlers.put(TopicNameIF.EVENT_REMOVE_VARIANT, new EH10());
    handlers.put(TopicIF.EVENT_ADD_OCCURRENCE, new EH13());
    handlers.put(TopicIF.EVENT_REMOVE_OCCURRENCE, new EH14());
    handlers.put(AssociationIF.EVENT_ADD_ROLE, new EH15());
    handlers.put(AssociationIF.EVENT_REMOVE_ROLE, new EH16());

    // Register as event listener
    Iterator<String> iter = handlers.keySet().iterator();
    while (iter.hasNext()) {
      emanager.addListener(this, iter.next());
    }

    // Make this object available to nested classes.
    otree = this;
  }
  
  // -----------------------------------------------------------------------------
  // EventManagerIF implementation
  // -----------------------------------------------------------------------------
  
  @Override
  public void addListener(EventListenerIF listener, String event) {
    // Adding itself causes infinite loops.
    if (listener == this) {
      return;
    }
    // Initialize event entry
    if (!listeners.containsKey(event)) {
      Set<EventListenerIF> newset = cfactory.makeSmallSet(); 
      listeners.put(event, newset);
	}
    // Add listener to event entry listeners collection
    listeners.get(event).add(listener);
  }

  @Override
  public void removeListener(EventListenerIF listener, String event) {
    if (listeners.containsKey(event)) {
      // Remove listener from event listeners collection
      Set<EventListenerIF> event_listeners = listeners.get(event);
      event_listeners.remove(listener);
      // If there are no more listeners, remove event entry.
      if (event_listeners.isEmpty()) {
        listeners.remove(event);
      }      
    }
  }
  
  // -----------------------------------------------------------------------------
  // EventListenerIF
  // -----------------------------------------------------------------------------

  @Override
  public void processEvent(Object object, String event, Object new_value, Object old_value) {
    if (handlers.containsKey(event)) {
      EventListenerIF handler = (EventListenerIF)handlers.get(event);
      handler.processEvent(object, event, new_value, old_value);
    }
  }
  
  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  protected abstract class EventHandler implements EventListenerIF, java.io.Serializable {
    @Override
    public abstract void processEvent(Object object, String event, Object new_value, Object old_value);

    /**
     * Inform object tree event listeners about object tree add event.
     */
    protected void treeAddEvent(Object parent, String event, Object added) {
      if (listeners.containsKey(event)) {
        // Loop over event listeners
        Set<EventListenerIF> event_listeners = listeners.get(event);
        Iterator<EventListenerIF> iter = event_listeners.iterator();
        while (iter.hasNext()) {
          // Notify listener
          iter.next().processEvent(parent, event, added, null);
        }
      }      
    }

    /**
     * Inform object tree event listeners about object tree add event.
     */
    protected void treeRemoveEvent(Object parent, String event, Object removed) {
      if (listeners.containsKey(event)) {
        // Loop over event listeners
        Set<EventListenerIF> event_listeners = listeners.get(event);
        Iterator<EventListenerIF> iter = event_listeners.iterator();
        while (iter.hasNext()) {
          // Notify listener
          iter.next().processEvent(parent, event, null, removed);
        }
      }      
    }
  }

  /**
   * EventHandler: TopicMapIF.addTopic
   */
  class EH01 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicIF added = (TopicIF)new_value;
      // Fire object added event
      treeAddEvent(object, TopicIF.EVENT_ADDED, added);
      // Add basenames
      Object[] basenames = added.getTopicNames().toArray();
      for (int i=0; i < basenames.length; i++) {
        otree.processEvent(added, TopicIF.EVENT_ADD_TOPICNAME, basenames[i], null);
      }
      // Add occurrences
      Object[] occurs = added.getOccurrences().toArray();
      for (int i=0; i < occurs.length; i++) {
        otree.processEvent(added, TopicIF.EVENT_ADD_OCCURRENCE, occurs[i], null);
      }
    }
  }
  /**
   * EventHandler: TopicMapIF.removeTopic
   */
  class EH02 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicIF removed = (TopicIF)old_value;
      // Fire tree event
      treeRemoveEvent(object, TopicIF.EVENT_REMOVED, removed);
      // Remove basenames
      Object[] basenames = removed.getTopicNames().toArray();
      for (int i=0; i < basenames.length; i++) {
        otree.processEvent(removed, TopicIF.EVENT_REMOVE_TOPICNAME, null, basenames[i]);
      }
      // Remove occurrences
      Object[] occurs = removed.getOccurrences().toArray();
      for (int i=0; i < occurs.length; i++) {
        otree.processEvent(removed, TopicIF.EVENT_REMOVE_OCCURRENCE, null, occurs[i]);
      }
    }
  }
  /**
   * EventHandler: TopicMapIF.addAssociation
   */
  class EH03 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationIF added = (AssociationIF)new_value;
      // Fire object added event
      treeAddEvent(object, AssociationIF.EVENT_ADDED, added);
      // Add association roles
      Object[] roles = added.getRoles().toArray();
      for (int i=0; i < roles.length; i++) {
        otree.processEvent(added, AssociationIF.EVENT_ADD_ROLE, roles[i], null);
      }
    }
  }
  /**
   * EventHandler: TopicMapIF.removeAssociation
   */
  class EH04 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationIF removed = (AssociationIF)old_value;
      // Fire tree event
      treeRemoveEvent(object, AssociationIF.EVENT_REMOVED, removed);
      // Remove association roles
      Object[] roles = removed.getRoles().toArray();
      for (int i=0; i < roles.length; i++) {
        otree.processEvent(removed, AssociationIF.EVENT_REMOVE_ROLE, null, roles[i]);
      }
    }
  }
  /**
   * EventHandler: TopicIF.addTopicName
   */
  class EH07 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicNameIF added = (TopicNameIF)new_value;
      // Fire object added event
      treeAddEvent(object, TopicNameIF.EVENT_ADDED, added);
      // Add variants
      Object[] variants = added.getVariants().toArray();
      for (int i=0; i < variants.length; i++) {
        otree.processEvent(added, TopicNameIF.EVENT_ADD_VARIANT, variants[i], null);
      }
    }
  }
  /**
   * EventHandler: TopicIF.removeTopicName
   */
  class EH08 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicNameIF removed = (TopicNameIF)old_value;
      // Fire tree event
      treeRemoveEvent(object, TopicNameIF.EVENT_REMOVED, removed);
      // Remove variants
      Object[] variants = removed.getVariants().toArray();
      for (int i=0; i < variants.length; i++) {
        otree.processEvent(removed, TopicNameIF.EVENT_REMOVE_VARIANT, null, variants[i]);
      }
    }
  }
  /**
   * EventHandler: TopicNameIF.addVariant
   */
  class EH09 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      VariantNameIF added = (VariantNameIF)new_value;
      // Fire object added event
      treeAddEvent(object, VariantNameIF.EVENT_ADDED, added);
    }
  }
  /**
   * EventHandler: TopicNameIF.removeVariant
   */
  class EH10 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      VariantNameIF removed = (VariantNameIF)old_value;
      // Fire tree event
      treeRemoveEvent(object, VariantNameIF.EVENT_REMOVED, removed);
    }
  }
  /**
   * EventHandler: TopicIF.addOccurrence
   */
  class EH13 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      OccurrenceIF added = (OccurrenceIF)new_value;
      // Fire object added event
      treeAddEvent(object, OccurrenceIF.EVENT_ADDED, added);
    }
  }
  /**
   * EventHandler: TopicIF.removeOccurrence
   */
  class EH14 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      OccurrenceIF removed = (OccurrenceIF)old_value;
      // Fire tree event
      treeRemoveEvent(object, OccurrenceIF.EVENT_REMOVED, removed);
    }
  }
  /**
   * EventHandler: AssociationIF.addRole
   */
  class EH15 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationRoleIF added = (AssociationRoleIF)new_value;
      // Fire object added event
      treeAddEvent(object, AssociationRoleIF.EVENT_ADDED, added);
    }
  }
  /**
   * EventHandler: AssociationIF.removeRole
   */
  class EH16 extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationRoleIF removed = (AssociationRoleIF)old_value;
      // Fire tree event
      treeRemoveEvent(object, AssociationRoleIF.EVENT_REMOVED, removed);
    }
  }
    
}
