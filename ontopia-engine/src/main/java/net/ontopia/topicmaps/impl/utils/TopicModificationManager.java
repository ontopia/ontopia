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
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.CollectionFactoryIF;

/**
 * INTERNAL: Event manager that fires TopicIF.modified events.</p>
 */

public class TopicModificationManager implements EventManagerIF, java.io.Serializable {

  protected Map<String, EventHandler> handlers; 
  protected Map<String, Set<EventListenerIF>> listeners;

  protected TopicModificationManager manager;
  protected CollectionFactoryIF cfactory;

  protected TopicHandler th;
  protected TopicNameHandler bh;
  protected VariantNameHandler vh;
  protected OccurrenceHandler oh;
  protected AssociationRoleHandler rh;
  protected AssociationHandler ah;
  protected TopicMapHandler mh;
  
  public TopicModificationManager(EventManagerIF emanager, CollectionFactoryIF cfactory) {
    // Setup 
    this.cfactory = cfactory;

    // Initialize listeners
    this.listeners = cfactory.makeLargeMap();
    
    // Initialize handlers map
    this.handlers = cfactory.makeLargeMap();

    // Initialize parent-child relationship event handlers
    this.th = new TopicHandler();
    handlers.put(TopicIF.EVENT_ADD_SUBJECTLOCATOR, th);
    handlers.put(TopicIF.EVENT_REMOVE_SUBJECTLOCATOR, th);
    handlers.put(TopicIF.EVENT_ADD_SUBJECTIDENTIFIER, th);
    handlers.put(TopicIF.EVENT_REMOVE_SUBJECTIDENTIFIER, th);
    handlers.put(TopicIF.EVENT_ADD_TOPICNAME, th);
    handlers.put(TopicIF.EVENT_REMOVE_TOPICNAME, th);
    handlers.put(TopicIF.EVENT_ADD_OCCURRENCE, th);
    handlers.put(TopicIF.EVENT_REMOVE_OCCURRENCE, th);
    handlers.put("TopicIF.addTheme", th);
    handlers.put("TopicIF.removeTheme", th);
    handlers.put(TopicIF.EVENT_ADD_TYPE, th);
    handlers.put(TopicIF.EVENT_REMOVE_TYPE, th);

    this.bh = new TopicNameHandler();
    handlers.put(TopicNameIF.EVENT_SET_VALUE, bh);
    handlers.put(TopicNameIF.EVENT_ADD_VARIANT, bh);
    handlers.put(TopicNameIF.EVENT_REMOVE_VARIANT, bh);
    handlers.put(TopicNameIF.EVENT_ADD_THEME, bh);
    handlers.put(TopicNameIF.EVENT_REMOVE_THEME, bh);
    handlers.put(TopicNameIF.EVENT_SET_TYPE, bh);

    this.vh = new VariantNameHandler();
    handlers.put(VariantNameIF.EVENT_SET_VALUE, vh);
    handlers.put(VariantNameIF.EVENT_ADD_THEME, vh);
    handlers.put(VariantNameIF.EVENT_REMOVE_THEME, vh);

    this.oh = new OccurrenceHandler();
    handlers.put(OccurrenceIF.EVENT_SET_VALUE, oh);
    handlers.put(OccurrenceIF.EVENT_ADD_THEME, oh);
    handlers.put(OccurrenceIF.EVENT_REMOVE_THEME, oh);
    handlers.put(OccurrenceIF.EVENT_SET_TYPE, oh);

    this.ah = new AssociationHandler();
    handlers.put(AssociationIF.EVENT_ADD_ROLE, ah);
    handlers.put(AssociationIF.EVENT_REMOVE_ROLE, ah);
    handlers.put(AssociationIF.EVENT_ADD_THEME, ah);
    handlers.put(AssociationIF.EVENT_REMOVE_THEME, ah);
    handlers.put(AssociationIF.EVENT_SET_TYPE, ah);

    this.rh = new AssociationRoleHandler();
    handlers.put(AssociationRoleIF.EVENT_SET_PLAYER, rh);
    handlers.put(AssociationRoleIF.EVENT_SET_TYPE, rh);

    this.mh = new TopicMapHandler();
    handlers.put(TopicMapIF.EVENT_REMOVE_ASSOCIATION, mh);

    TMObjectHandler xh = new TMObjectHandler();
    handlers.put(TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, xh);
    handlers.put(TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, xh);

    // Register as event listener
    Iterator<String> iter = handlers.keySet().iterator();
    while (iter.hasNext()) {
      emanager.addListener(this, iter.next());
    }

    // Make this object available to nested classes.
    manager = this;
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
      Set<EventListenerIF> event_listeners  = listeners.get(event);
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
      EventListenerIF handler = handlers.get(event);
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
    protected void topicModified(TopicIF topic) {
      String event = TopicIF.EVENT_MODIFIED;
      if (listeners.containsKey(event)) {
        // Loop over event listeners
        Set<EventListenerIF> event_listeners = listeners.get(event);
        Iterator<EventListenerIF> iter = event_listeners.iterator();
        while (iter.hasNext()) {
          // Notify listener
         iter.next().processEvent(topic, event, null, null);
        }
      }      
    }
  }

  /**
   * EventHandler: Topic.*
   */
  class TopicHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicIF topic = (TopicIF)object;
      topicModified(topic);
    }
  }

  /**
   * EventHandler: TopicName.*
   */
  class TopicNameHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicNameIF bn = (TopicNameIF)object;
      TopicIF topic = bn.getTopic();
      if (topic != null) {
        topicModified(topic);
      }
    }
  }

  /**
   * EventHandler: VariantName.*
   */
  class VariantNameHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      VariantNameIF vn = (VariantNameIF)object;
      TopicNameIF bn = vn.getTopicName();
      if (bn != null) {
        TopicIF topic = bn.getTopic();
        if (topic != null) {
          topicModified(topic);
        }
      }
    }
  }

  /**
   * EventHandler: Occurrence.*
   */
  class OccurrenceHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      OccurrenceIF occ = (OccurrenceIF)object;
      TopicIF topic = occ.getTopic();
      if (topic != null) {
        topicModified(topic);
      }
    }
  }

  /**
   * EventHandler: AssociationRole.*
   */
  class AssociationRoleHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationRoleIF role = (AssociationRoleIF)object;
      AssociationIF assoc = role.getAssociation();

      if (event.equals(AssociationRoleIF.EVENT_SET_PLAYER)) {                 
        if (old_value != null) {
          topicModified((TopicIF)old_value);
        }
        if (new_value != null) {
          topicModified((TopicIF)new_value);
        }
      } else {
        TopicIF topic = role.getPlayer();
        if (topic != null) {
          topicModified(topic);
        }
      }

      if (assoc != null) {
        Iterator<AssociationRoleIF> iter = assoc.getRoles().iterator();
        while (iter.hasNext()) {
          AssociationRoleIF orole = iter.next();
          if (!orole.equals(role)) {
            TopicIF otopic = orole.getPlayer();
            if (otopic != null) {
              topicModified(otopic);
            }
          }
        }
      }
    }
  }

  /**
   * EventHandler: Association.*
   */
  class AssociationHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationIF assoc = (AssociationIF)object;
      Iterator<AssociationRoleIF> iter = assoc.getRoles().iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = iter.next();
        TopicIF topic = role.getPlayer();
        if (topic != null) {
          topicModified(topic);
        }          
      }
    }
  }

  /**
   * EventHandler: TopicMap.*
   */
  class TopicMapHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      if (event.equals(TopicMapIF.EVENT_REMOVE_ASSOCIATION)) {
        AssociationIF assoc = (AssociationIF)old_value;
        Iterator<AssociationRoleIF> iter = assoc.getRoles().iterator();
        while (iter.hasNext()) {
          AssociationRoleIF role = iter.next();
          TopicIF topic = role.getPlayer();
          if (topic != null) {
            topicModified(topic);
          }          
        }
      }
    }
  }

  /**
   * EventHandler: TMObjectIF.*
   */
  class TMObjectHandler extends EventHandler {

    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      if (object instanceof TopicNameIF) {
        manager.bh.processEvent(object, event, new_value, old_value);
      } else if (object instanceof VariantNameIF) {
        manager.vh.processEvent(object, event, new_value, old_value);
      } else if (object instanceof OccurrenceIF) {
        manager.oh.processEvent(object, event, new_value, old_value);
      } else if (object instanceof AssociationRoleIF) {
        manager.rh.processEvent(object, event, new_value, old_value);
      } else if (object instanceof AssociationIF) {
        manager.ah.processEvent(object, event, new_value, old_value);
      } else if (object instanceof TopicIF) {
        manager.th.processEvent(object, event, new_value, old_value);
      } else if (object instanceof TopicMapIF) {
        manager.mh.processEvent(object, event, new_value, old_value);
      }
    }
  }
    
}
