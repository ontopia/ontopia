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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Class that maintains indexes for use with the TopicMapIF locator
 * lookup methods. This is especially useful in the cases where the topic map
 * object cannot use queries to do the lookups.
 * </p>
 * 
 * This class uses the event model to maintain its indexes.
 * </p>
 */

public abstract class AbstractSubjectIdentityCache implements EventListenerIF,
    java.io.Serializable {
  private static final String ANOTHER_TOPIC = "Another topic ";

  protected Map<String, EventHandler> handlers;

  public AbstractSubjectIdentityCache(Map<String, EventHandler> handlers) {
    this.handlers = handlers;
  }

  /**
   * INTERNAL: Registers the subject identity cache listeners with the default
   * event manager and the object tree event managers.
   * 
   * @param emanager The default event manager.
   * @param otree The object tree manager.
   */
  public void registerListeners(EventManagerIF emanager, EventManagerIF otree) {

    // Initialize object tree event handlers [objects added or removed]
    otree.addListener(new TopicAddedHandler(), TopicIF.EVENT_ADDED);
    otree.addListener(new TopicRemovedHandler(), TopicIF.EVENT_REMOVED);

    EventHandler oah = new TMObjectAddedHandler();
    EventHandler orh = new TMObjectRemovedHandler();

    otree.addListener(oah, AssociationIF.EVENT_ADDED);
    otree.addListener(orh, AssociationIF.EVENT_REMOVED);
    otree.addListener(oah, AssociationRoleIF.EVENT_ADDED);
    otree.addListener(orh, AssociationRoleIF.EVENT_REMOVED);
    otree.addListener(oah, TopicNameIF.EVENT_ADDED);
    otree.addListener(orh, TopicNameIF.EVENT_REMOVED);
    otree.addListener(oah, OccurrenceIF.EVENT_ADDED);
    otree.addListener(orh, OccurrenceIF.EVENT_REMOVED);
    otree.addListener(oah, VariantNameIF.EVENT_ADDED);
    otree.addListener(orh, VariantNameIF.EVENT_REMOVED);

    // Initialize object property event handlers
    handlers.put(TopicIF.EVENT_ADD_SUBJECTLOCATOR, new TopicIF_addSubjectLocator());
    handlers.put(TopicIF.EVENT_REMOVE_SUBJECTLOCATOR, new TopicIF_removeSubjectLocator());

    handlers.put(TopicIF.EVENT_ADD_SUBJECTIDENTIFIER, new TopicIF_addSubjectIdentifier());
    handlers.put(TopicIF.EVENT_REMOVE_SUBJECTIDENTIFIER, new TopicIF_removeSubjectIdentifier());

    handlers.put(TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, new TMObjectIF_addItemIdentifier());
    handlers.put(TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, new TMObjectIF_removeItemIdentifier());

    // Register as event listener
    Iterator<String> iter = handlers.keySet().iterator();
    while (iter.hasNext()) {
      emanager.addListener(this, iter.next());
    }
  }

  // -----------------------------------------------------------------------------
  // TopicMapIF locator lookup methods
  // -----------------------------------------------------------------------------

  public abstract TMObjectIF getObjectById(String object_id);

  public abstract TMObjectIF getObjectByItemIdentifier(LocatorIF locator);

  public abstract TopicIF getTopicBySubjectLocator(LocatorIF locator);

  public abstract TopicIF getTopicBySubjectIdentifier(LocatorIF locator);

  // -----------------------------------------------------------------------------
  // Object registration methods
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Register the object with the identity map. Does nothing by
   * default.
   */
  protected void registerObject(TMObjectIF object) {
    // no-op
  }

  /**
   * INTERNAL: Unregister the object with the identity map. Does nothing by
   * default.
   */
  protected void unregisterObject(TMObjectIF object) {
    // no-op
  }

  // -----------------------------------------------------------------------------
  // Event handler methods
  // -----------------------------------------------------------------------------

  protected abstract TMObjectIF _getObjectByItemIdentifier(LocatorIF source_locator);

  protected abstract void registerSourceLocator(LocatorIF source_locator,
      TMObjectIF object);

  protected abstract void unregisterSourceLocator(LocatorIF source_locator);

  protected abstract TopicIF _getTopicBySubjectIdentifier(LocatorIF subject_indicator);

  protected abstract void registerSubjectIndicator(LocatorIF subject_indicator,
      TopicIF object);

  protected abstract void unregisterSubjectIndicator(LocatorIF subject_indicator);

  protected abstract TopicIF _getTopicBySubjectLocator(LocatorIF subject);

  protected abstract void registerSubject(LocatorIF subject, TopicIF object);

  protected abstract void unregisterSubject(LocatorIF subject);

  // -----------------------------------------------------------------------------
  // EventListenerIF
  // -----------------------------------------------------------------------------

  @Override
  public void processEvent(Object object, String event, Object new_value,
      Object old_value) {
    if (handlers.containsKey(event)) {
      handlers.get(event).processEvent(object, event, new_value, old_value);
    }
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  protected abstract class EventHandler implements EventListenerIF,
      java.io.Serializable {
    @Override
    public abstract void processEvent(Object object, String event,
        Object new_value, Object old_value);

    protected void addEvent(Object object, String event, Object value) {
      // if (!handlers.containsKey(event)) System.out.println("+event> " +
      // event);
      handlers.get(event).processEvent(object, event,
          value, null);
    }

    protected void removeEvent(Object object, String event, Object value) {
      // if (!handlers.containsKey(event)) System.out.println("-event> " +
      // event);
      handlers.get(event).processEvent(object, event, null, value);
    }
  }

  /**
   * EventHandler: TopicIF.added
   */
  class TopicAddedHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      TopicIF added = (TopicIF) new_value;
      // Register object
      registerObject(added);
      // Add subject locators
      Object[] subjects = added.getSubjectLocators().toArray();
      for (int i = 0; i < subjects.length; i++) {
        addEvent(added, TopicIF.EVENT_ADD_SUBJECTLOCATOR, subjects[i]);
      }
      // Add indicators
      Object[] indicators = added.getSubjectIdentifiers().toArray();
      for (int i = 0; i < indicators.length; i++) {
        addEvent(added, TopicIF.EVENT_ADD_SUBJECTIDENTIFIER, indicators[i]);
      }
      // Add source locators
      Object[] sources = added.getItemIdentifiers().toArray();
      for (int i = 0; i < sources.length; i++) {
        addEvent(added, TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, sources[i]);
      }
    }
  }

  /**
   * EventHandler: TopicIF.removed
   */
  class TopicRemovedHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      TopicIF removed = (TopicIF) old_value;
      // Remove subject locators
      Object[] subjects = removed.getSubjectLocators().toArray();
      for (int i = 0; i < subjects.length; i++) {
        removeEvent(removed, TopicIF.EVENT_REMOVE_SUBJECTLOCATOR, subjects[i]);
      }
      // Remove indicators
      Object[] indicators = removed.getSubjectIdentifiers().toArray();
      for (int i = 0; i < indicators.length; i++) {
        removeEvent(removed, TopicIF.EVENT_REMOVE_SUBJECTIDENTIFIER, indicators[i]);
      }
      // Remove source locators
      Object[] sources = removed.getItemIdentifiers().toArray();
      for (int i = 0; i < sources.length; i++) {
        removeEvent(removed, TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, sources[i]);
      }
      // Unregister object
      unregisterObject(removed);
    }
  }

  /**
   * EventHandler: TMObjectIF.added
   */
  class TMObjectAddedHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      TMObjectIF added = (TMObjectIF) new_value;
      // Register object
      registerObject(added);
      // Add source locators
      Object[] sources = added.getItemIdentifiers().toArray();
      for (int i = 0; i < sources.length; i++) {
        addEvent(added, TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, sources[i]);
      }
    }
  }

  /**
   * EventHandler: TMObjectIF.removed
   */
  class TMObjectRemovedHandler extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      TMObjectIF removed = (TMObjectIF) old_value;
      // Remove source locators
      Object[] sources = removed.getItemIdentifiers().toArray();
      for (int i = 0; i < sources.length; i++) {
        removeEvent(removed, TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, sources[i]);
      }
      // Unregister object
      unregisterObject(removed);
    }
  }

  /**
   * EventHandler: TopicIF.addSubjectLocator
   */
  class TopicIF_addSubjectLocator extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {

      // Check subject locator uniqueness
      TopicIF existing = _getTopicBySubjectLocator((LocatorIF)new_value);
      if (existing != null && !existing.equals(object)) {
        throw new UniquenessViolationException(ANOTHER_TOPIC + existing
            + " already has this subject locator: " + new_value + " ("
            + object + ")");
      }

      // Register new subject locator
      registerSubject((LocatorIF)new_value, (TopicIF)object);
    }
  }

  /**
   * EventHandler: TopicIF.removeSubjectLocator
   */
  class TopicIF_removeSubjectLocator extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      // Unregister subject locator
      unregisterSubject((LocatorIF)old_value);
    }
  }

  /**
   * EventHandler: TopicIF.addSubjectIdentifier
   */
  class TopicIF_addSubjectIdentifier extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {

      // Check indicator uniqueness
      TopicIF existing = _getTopicBySubjectIdentifier((LocatorIF)new_value);
      if (existing != null && existing != object) {
        throw new UniquenessViolationException(ANOTHER_TOPIC + existing
            + " already has this subject identifier: " + new_value + " ("
            + object + ")");
      }
      // Check for source locator clash
      TMObjectIF existing_tmo = _getObjectByItemIdentifier((LocatorIF)new_value);
      if (existing_tmo != null && !existing_tmo.equals(object)
          && (existing_tmo instanceof TopicIF)) {
        throw new UniquenessViolationException(ANOTHER_TOPIC + existing_tmo
            + " already has this subject identifier as its item identifier: "
            + new_value + " (" + object + ")");
      }

      // Register new subject indicator
      registerSubjectIndicator((LocatorIF)new_value, (TopicIF)object);
    }
  }

  /**
   * EventHandler: TopicIF.removeSubjectIdentifier
   */
  class TopicIF_removeSubjectIdentifier extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      // Unregister subject indicator
      unregisterSubjectIndicator((LocatorIF)old_value);
    }
  }

  /**
   * EventHandler: TMObjectIF.addItemIdentifier
   */
  class TMObjectIF_addItemIdentifier extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) throws ConstraintViolationException {

      // Check source locator uniqueness
      TMObjectIF existing = _getObjectByItemIdentifier((LocatorIF)new_value);
      // NOTE: we should not get this far if existing == object,
      // because we're checking for this in
      // TMObject.addItemIdentifier(). If we get here it is often an
      // indication of the database using case insensitive =
      // comparisions.
      if (existing != null && existing != object) {
        throw new UniquenessViolationException("Another object " + existing
            + " already has this item identifier: " + new_value + " (" + object
            + ") " + existing.equals(object));
      }
      
      // Check for subject identifier clash
      existing = _getTopicBySubjectIdentifier((LocatorIF)new_value);
      if (existing != null && !existing.equals(object) && (object instanceof TopicIF)) {
        throw new UniquenessViolationException(ANOTHER_TOPIC + existing
            + " already has this item identifier as its subject identifier: "
            + new_value + " (" + object + ")");
      }

      // Register new source locator
      registerSourceLocator((LocatorIF)new_value, (TMObjectIF)object);
    }
  }

  /**
   * EventHandler: TMObjectIF.removeItemIdentifier
   */
  class TMObjectIF_removeItemIdentifier extends EventHandler {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      // Unregister source locator
      unregisterSourceLocator((LocatorIF)old_value);
    }
  }
}
