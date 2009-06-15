
// $Id: AbstractSubjectIdentityCache.java,v 1.19 2009/02/27 11:58:49 lars.garshol Exp $

package net.ontopia.topicmaps.impl.utils;

import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.UniquenessViolationException;

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

  protected Map handlers;

  public AbstractSubjectIdentityCache(Map handlers) {
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
    otree.addListener(new TopicAddedHandler(), "TopicIF.added");
    otree.addListener(new TopicRemovedHandler(), "TopicIF.removed");

    EventHandler oah = new TMObjectAddedHandler();
    EventHandler orh = new TMObjectRemovedHandler();

    otree.addListener(oah, "AssociationIF.added");
    otree.addListener(orh, "AssociationIF.removed");
    otree.addListener(oah, "AssociationRoleIF.added");
    otree.addListener(orh, "AssociationRoleIF.removed");
    otree.addListener(oah, "TopicNameIF.added");
    otree.addListener(orh, "TopicNameIF.removed");
    otree.addListener(oah, "OccurrenceIF.added");
    otree.addListener(orh, "OccurrenceIF.removed");
    otree.addListener(oah, "VariantNameIF.added");
    otree.addListener(orh, "VariantNameIF.removed");

    // Initialize object property event handlers
    handlers.put("TopicIF.addSubjectLocator", new TopicIF_addSubjectLocator());
    handlers.put("TopicIF.removeSubjectLocator", new TopicIF_removeSubjectLocator());

    handlers.put("TopicIF.addSubjectIdentifier", new TopicIF_addSubjectIdentifier());
    handlers.put("TopicIF.removeSubjectIdentifier", new TopicIF_removeSubjectIdentifier());

    handlers.put("TMObjectIF.addItemIdentifier", new TMObjectIF_addItemIdentifier());
    handlers.put("TMObjectIF.removeItemIdentifier", new TMObjectIF_removeItemIdentifier());

    // Register as event listener
    Iterator iter = handlers.keySet().iterator();
    while (iter.hasNext()) {
      emanager.addListener(this, (String) iter.next());
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
  }

  /**
   * INTERNAL: Unregister the object with the identity map. Does nothing by
   * default.
   */
  protected void unregisterObject(TMObjectIF object) {
  }

  // -----------------------------------------------------------------------------
  // Event handler methods
  // -----------------------------------------------------------------------------

  protected abstract Object _getObjectByItemIdentifier(Object source_locator);

  protected abstract void registerSourceLocator(Object source_locator,
      Object object);

  protected abstract void unregisterSourceLocator(Object source_locator);

  protected abstract Object _getTopicBySubjectIdentifier(Object subject_indicator);

  protected abstract void registerSubjectIndicator(Object subject_indicator,
      Object object);

  protected abstract void unregisterSubjectIndicator(Object subject_indicator);

  protected abstract Object _getTopicBySubjectLocator(Object subject);

  protected abstract void registerSubject(Object subject, Object object);

  protected abstract void unregisterSubject(Object subject);

  // -----------------------------------------------------------------------------
  // EventListenerIF
  // -----------------------------------------------------------------------------

  public void processEvent(Object object, String event, Object new_value,
      Object old_value) {
    if (handlers.containsKey(event)) {
      ((EventListenerIF) handlers.get(event)).processEvent(object, event,
          new_value, old_value);
    }
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  protected abstract class EventHandler implements EventListenerIF,
      java.io.Serializable {
    public abstract void processEvent(Object object, String event,
        Object new_value, Object old_value);

    protected void addEvent(Object object, String event, Object value) {
      // if (!handlers.containsKey(event)) System.out.println("+event> " +
      // event);
      ((EventListenerIF) handlers.get(event)).processEvent(object, event,
          value, null);
    }

    protected void removeEvent(Object object, String event, Object value) {
      // if (!handlers.containsKey(event)) System.out.println("-event> " +
      // event);
      ((EventListenerIF) handlers.get(event)).processEvent(object, event, null,
          value);
    }
  }

  /**
   * EventHandler: TopicIF.added
   */
  class TopicAddedHandler extends EventHandler {
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      TopicIF added = (TopicIF) new_value;
      // Register object
      registerObject(added);
      // Add subject locators
      Object[] subjects = added.getSubjectLocators().toArray();
      for (int i = 0; i < subjects.length; i++)
        addEvent(added, "TopicIF.addSubjectLocator", subjects[i]);
      // Add indicators
      Object[] indicators = added.getSubjectIdentifiers().toArray();
      for (int i = 0; i < indicators.length; i++)
        addEvent(added, "TopicIF.addSubjectIdentifier", indicators[i]);
      // Add source locators
      Object[] sources = added.getItemIdentifiers().toArray();
      for (int i = 0; i < sources.length; i++)
        addEvent(added, "TMObjectIF.addItemIdentifier", sources[i]);
    }
  }

  /**
   * EventHandler: TopicIF.removed
   */
  class TopicRemovedHandler extends EventHandler {
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      TopicIF removed = (TopicIF) old_value;
      // Remove subject locators
      Object[] subjects = removed.getSubjectLocators().toArray();
      for (int i = 0; i < subjects.length; i++)
        removeEvent(removed, "TopicIF.removeSubjectLocator", subjects[i]);
      // Remove indicators
      Object[] indicators = removed.getSubjectIdentifiers().toArray();
      for (int i = 0; i < indicators.length; i++)
        removeEvent(removed, "TopicIF.removeSubjectIdentifier", indicators[i]);
      // Remove source locators
      Object[] sources = removed.getItemIdentifiers().toArray();
      for (int i = 0; i < sources.length; i++)
        removeEvent(removed, "TMObjectIF.removeItemIdentifier", sources[i]);
      // Unregister object
      unregisterObject(removed);
    }
  }

  /**
   * EventHandler: TMObjectIF.added
   */
  class TMObjectAddedHandler extends EventHandler {
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      TMObjectIF added = (TMObjectIF) new_value;
      // Register object
      registerObject(added);
      // Add source locators
      Object[] sources = added.getItemIdentifiers().toArray();
      for (int i = 0; i < sources.length; i++)
        addEvent(added, "TMObjectIF.addItemIdentifier", sources[i]);
    }
  }

  /**
   * EventHandler: TMObjectIF.removed
   */
  class TMObjectRemovedHandler extends EventHandler {
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      TMObjectIF removed = (TMObjectIF) old_value;
      // Remove source locators
      Object[] sources = removed.getItemIdentifiers().toArray();
      for (int i = 0; i < sources.length; i++)
        removeEvent(removed, "TMObjectIF.removeItemIdentifier", sources[i]);
      // Unregister object
      unregisterObject(removed);
    }
  }

  /**
   * EventHandler: TopicIF.addSubjectLocator
   */
  class TopicIF_addSubjectLocator extends EventHandler {
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {

      // Check subject locator uniqueness
      Object existing = _getTopicBySubjectLocator(new_value);
      if (existing != null && existing != object)
        throw new UniquenessViolationException("Another topic " + existing
            + " already has this subject locator: " + new_value + " ("
            + object + ")");

      // Register new subject locator
      registerSubject(new_value, object);
    }
  }

  /**
   * EventHandler: TopicIF.removeSubjectLocator
   */
  class TopicIF_removeSubjectLocator extends EventHandler {
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      // Unregister subject locator
      unregisterSubject(old_value);
    }
  }

  /**
   * EventHandler: TopicIF.addSubjectIdentifier
   */
  class TopicIF_addSubjectIdentifier extends EventHandler {
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {

      // Check indicator uniqueness
      Object existing = _getTopicBySubjectIdentifier(new_value);
      if (existing != null && existing != object)
        throw new UniquenessViolationException("Another topic " + existing
            + " already has this subject identifier: " + new_value + " ("
            + object + ")");
      // Check for source locator clash
      existing = _getObjectByItemIdentifier(new_value);
      if (existing != null && existing != object
          && (existing instanceof TopicIF))
        throw new UniquenessViolationException("Another topic " + existing
            + " already has this subject identifier as its item identifier: "
            + new_value + " (" + object + ")");

      // Register new subject indicator
      registerSubjectIndicator(new_value, object);
    }
  }

  /**
   * EventHandler: TopicIF.removeSubjectIdentifier
   */
  class TopicIF_removeSubjectIdentifier extends EventHandler {
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      // Unregister subject indicator
      unregisterSubjectIndicator(old_value);
    }
  }

  /**
   * EventHandler: TMObjectIF.addItemIdentifier
   */
  class TMObjectIF_addItemIdentifier extends EventHandler {
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) throws ConstraintViolationException {

      // Check source locator uniqueness
      Object existing = _getObjectByItemIdentifier(new_value);
      // NOTE: we should not get this far if existing == object,
      // because we're checking for this in
      // TMObject.addItemIdentifier(). If we get here it is often an
      // indication of the database using case insensitive =
      // comparisions.
      if (existing != null && existing != object)
        throw new UniquenessViolationException("Another object " + existing
            + " already has this item identifier: " + new_value + " (" + object
            + ") " + (existing == object));
      // Check for subject indicator clash
      existing = _getTopicBySubjectIdentifier(new_value);
      if (existing != null && existing != object && (object instanceof TopicIF))
        throw new UniquenessViolationException("Another topic " + existing
            + " already has this item identifier as its subject identifier: "
            + new_value + " (" + object + ")");

      // Register new source locator
      registerSourceLocator(new_value, object);
    }
  }

  /**
   * EventHandler: TMObjectIF.removeItemIdentifier
   */
  class TMObjectIF_removeItemIdentifier extends EventHandler {
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      // Unregister source locator
      unregisterSourceLocator(old_value);
    }
  }
}
