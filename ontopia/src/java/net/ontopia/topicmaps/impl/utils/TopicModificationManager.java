
// $Id: TopicModificationManager.java,v 1.9 2008/06/13 08:17:52 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.CollectionFactoryIF;

/**
 * INTERNAL: Event manager that fires TopicIF.modified events.</p>
 */

public class TopicModificationManager implements EventManagerIF, java.io.Serializable {

  protected Map handlers; 
  protected Map listeners;

  protected TopicModificationManager manager;
  protected CollectionFactoryIF cfactory;

  protected TopicHandler th;
  protected TopicNameHandler bh;
  protected VariantNameHandler vh;
  protected OccurrenceHandler oh;
  protected AssociationRoleHandler rh;
  protected AssociationHandler ah;
  
  public TopicModificationManager(EventManagerIF emanager, CollectionFactoryIF cfactory) {
    // Setup 
    this.cfactory = cfactory;

    // Initialize listeners
    this.listeners = cfactory.makeLargeMap();
    
    // Initialize handlers map
    this.handlers = cfactory.makeLargeMap();

    // Initialize parent-child relationship event handlers
    this.th = new TopicHandler();
    handlers.put("TopicIF.addSubjectLocator", th);
    handlers.put("TopicIF.removeSubjectLocator", th);
    handlers.put("TopicIF.addSubjectIdentifier", th);
    handlers.put("TopicIF.removeSubjectIdentifier", th);
    handlers.put("TopicIF.addTopicName", th);
    handlers.put("TopicIF.removeTopicName", th);
    handlers.put("TopicIF.addOccurrence", th);
    handlers.put("TopicIF.removeOccurrence", th);
    handlers.put("TopicIF.addTheme", th);
    handlers.put("TopicIF.removeTheme", th);
    handlers.put("TopicIF.addType", th);
    handlers.put("TopicIF.removeType", th);

    this.bh = new TopicNameHandler();
    handlers.put("TopicNameIF.setValue", bh);
    handlers.put("TopicNameIF.addVariant", bh);
    handlers.put("TopicNameIF.removeVariant", bh);
    handlers.put("TopicNameIF.addTheme", bh);
    handlers.put("TopicNameIF.removeTheme", bh);
    handlers.put("TopicNameIF.setType", bh);

    this.vh = new VariantNameHandler();
    handlers.put("VariantNameIF.setValue", vh);
    handlers.put("VariantNameIF.addTheme", vh);
    handlers.put("VariantNameIF.removeTheme", vh);

    this.oh = new OccurrenceHandler();
    handlers.put("OccurrenceIF.setValue", oh);
    handlers.put("OccurrenceIF.addTheme", oh);
    handlers.put("OccurrenceIF.removeTheme", oh);
    handlers.put("OccurrenceIF.setType", oh);

    this.ah = new AssociationHandler();
    handlers.put("AssociationIF.addRole", ah);
    handlers.put("AssociationIF.removeRole", ah);
    handlers.put("AssociationIF.addTheme", ah);
    handlers.put("AssociationIF.removeTheme", ah);
    handlers.put("AssociationIF.setType", ah);

    this.rh = new AssociationRoleHandler();
    handlers.put("AssociationRoleIF.setPlayer", rh);
    handlers.put("AssociationRoleIF.setType", rh);

    TMObjectHandler xh = new TMObjectHandler();
    handlers.put("TMObjectIF.addItemIdentifier", xh);
    handlers.put("TMObjectIF.removeItemIdentifier", xh);

    // Register as event listener
    Iterator iter = handlers.keySet().iterator();
    while (iter.hasNext()) {
      emanager.addListener(this, (String)iter.next());
    }

    // Make this object available to nested classes.
    manager = this;
  }
  
  // -----------------------------------------------------------------------------
  // EventManagerIF implementation
  // -----------------------------------------------------------------------------
  
  public void addListener(EventListenerIF listener, String event) {
    // Adding itself causes infinite loops.
    if (listener == this) return;
    // Initialize event entry
    if (!listeners.containsKey(event))
      listeners.put(event, cfactory.makeSmallSet());
    // Add listener to event entry listeners collection
    ((Set)listeners.get(event)).add(listener);
  }

  public void removeListener(EventListenerIF listener, String event) {
    if (listeners.containsKey(event)) {
      // Remove listener from event listeners collection
      Set event_listeners  = (Set)listeners.get(event);
      event_listeners.remove(listener);
      // If there are no more listeners, remove event entry.
      if (event_listeners.isEmpty()) listeners.remove(event);      
    }
  }
  
  // -----------------------------------------------------------------------------
  // EventListenerIF
  // -----------------------------------------------------------------------------

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
    public abstract void processEvent(Object object, String event, Object new_value, Object old_value);

    /**
     * Inform object tree event listeners about object tree add event.
     */
    protected void topicModified(TopicIF topic) {
      String event = "TopicIF.modified";
      if (listeners.containsKey(event)) {
        // Loop over event listeners
        Set event_listeners = (Set)listeners.get(event);
        Iterator iter = event_listeners.iterator();
        while (iter.hasNext()) {
          // Notify listener
          ((EventListenerIF)iter.next()).processEvent(topic, event, null, null);
        }
      }      
    }
  }

  /**
   * EventHandler: Topic.*
   */
  class TopicHandler extends EventHandler {
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicIF topic = (TopicIF)object;
      topicModified(topic);
    }
  }

  /**
   * EventHandler: TopicName.*
   */
  class TopicNameHandler extends EventHandler {
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicNameIF bn = (TopicNameIF)object;
      TopicIF topic = bn.getTopic();
      if (topic != null)
        topicModified(topic);
    }
  }

  /**
   * EventHandler: VariantName.*
   */
  class VariantNameHandler extends EventHandler {
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      VariantNameIF vn = (VariantNameIF)object;
      TopicNameIF bn = vn.getTopicName();
      if (bn != null) {
        TopicIF topic = bn.getTopic();
        if (topic != null)
          topicModified(topic);
      }
    }
  }

  /**
   * EventHandler: Occurrence.*
   */
  class OccurrenceHandler extends EventHandler {
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      OccurrenceIF occ = (OccurrenceIF)object;
      TopicIF topic = occ.getTopic();
      if (topic != null)
        topicModified(topic);
    }
  }

  /**
   * EventHandler: AssociationRole.*
   */
  class AssociationRoleHandler extends EventHandler {
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationRoleIF role = (AssociationRoleIF)object;
      AssociationIF assoc = role.getAssociation();
      if (assoc != null) {
        Iterator iter = assoc.getRoles().iterator();
        while (iter.hasNext()) {
          AssociationRoleIF orole = (AssociationRoleIF)iter.next();
          TopicIF topic = orole.getPlayer();
          if (topic != null)
            topicModified(topic);          
        }
      } else {
        TopicIF topic = role.getPlayer();
        if (topic != null)
          topicModified(topic);
      }        
    }
  }

  /**
   * EventHandler: Association.*
   */
  class AssociationHandler extends EventHandler {
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationIF assoc = (AssociationIF)object;
      Iterator iter = assoc.getRoles().iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF)iter.next();
        TopicIF topic = role.getPlayer();
        if (topic != null)
          topicModified(topic);          
      }
    }
  }

  /**
   * EventHandler: TMObjectIF.*
   */
  class TMObjectHandler extends EventHandler {

    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      if (object instanceof TopicNameIF)
        manager.bh.processEvent(object, event, new_value, old_value);
      else if (object instanceof VariantNameIF)
        manager.vh.processEvent(object, event, new_value, old_value);
      else if (object instanceof OccurrenceIF)
        manager.oh.processEvent(object, event, new_value, old_value);
      else if (object instanceof AssociationRoleIF)
        manager.rh.processEvent(object, event, new_value, old_value);
      else if (object instanceof AssociationIF)
        manager.ah.processEvent(object, event, new_value, old_value);
      else if (object instanceof TopicIF)
        manager.th.processEvent(object, event, new_value, old_value);
    }
  }
    
}
