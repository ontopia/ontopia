
// $Id: TopicMap.java,v 1.95 2008/06/13 08:36:26 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.utils.EventListenerIF;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.utils.CollectionFactoryIF;
import net.ontopia.utils.UniqueSet;

/**
 * INTERNAL: The basic topic map implementation.
 */

public class TopicMap extends TMObject implements TopicMapIF, EventManagerIF {

  static final long serialVersionUID = -1334945778216658155L;

  transient InMemoryTopicMapTransaction txn;  
  transient CollectionFactoryIF cfactory;
  public transient UniqueSet<TopicIF> setpool = new UniqueSet<TopicIF>();
  
  protected transient SubjectIdentityCache sicache;

	protected TopicIF reifier;
  protected UniqueSet<TopicIF> scope;
  protected Set<TopicIF> topics;
  protected Set<AssociationIF> assocs;

  //protected Map object_ids; // unused
  //protected Map id_objects; // unused

  protected Map<String, EventListenerIF[]> listeners;
  
  TopicMap(InMemoryTopicMapTransaction txn) {
    this.txn = txn;
    this.topicmap = this;
    this.parent = this;
    this.cfactory = txn.getCollectionFactory();
    
    //object_ids = cfactory.makeLargeMap(); // unused
    //id_objects = cfactory.makeLargeMap(); // unused

    Set<TopicIF> empty = Collections.emptySet();
    scope = setpool.get(empty);
    topics = cfactory.makeLargeSet();
    assocs = cfactory.makeLargeSet();

    // Initialize listeners
    listeners = cfactory.makeLargeMap();    
  }
  
  // -----------------------------------------------------------------------------
  // TMObjectIF implementation
  // -----------------------------------------------------------------------------
  
  public TopicMapIF getTopicMap() {
    return this;
  }
  
  // -----------------------------------------------------------------------------
  // TopicMapIF implementation
  // -----------------------------------------------------------------------------

  public TopicMapStoreIF getStore() {
    return txn.getStore();
  }

  public TopicMapTransactionIF getTransaction() {
    return txn;
  }

  public TopicMapBuilderIF getBuilder() {
    return getTransaction().getBuilder();
  }

  public Object getIndex(String name) {
    return getTransaction().getIndexManager().getIndex(name);
  }

  SubjectIdentityCache getSubjectIdentityCache() {
    return sicache;
  }
  
  void setSubjectIdentityCache(SubjectIdentityCache sicache) {
    // Unregister topic map with old subject identity cache
    if (this.sicache != null)
      this.sicache.unregisterObject(this);
    // Register topic map with new subject identity cache
    sicache.registerObject(this);
    this.sicache = sicache;
  }
  
  public Collection<TopicIF> getTopics() {
    return Collections.unmodifiableSet(topics);
  }

  /**
   * Adds a topic to the set of topics.
   */
  public void addTopic(TopicIF _topic) {
    Topic topic = (Topic)_topic;
    // Check to see if topic is already a member of this topic map
    if (topic.parent == this)
      return;
    // Check if used elsewhere.
    if (topic.parent != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    // Notify listeners
    fireEvent("TopicMapIF.addTopic", topic, null);    
    // Set topic map property
    topic.setTopicMap(this);
    // Register topic with topic map
    topics.add(topic);
    // Notify topic listener
    txn.te.addedTopic(topic);

    // // Assign object id. Note that this is an exception to the object
    // // assignment rule. Only topics will be assigned ids when they're
    // // added to the topic map. The was added because of bug #89.
    // getObjectId(topic); // Asking for the object id is the same as assigning
  }

  /**
   * Removes a topic from the set of topics.
   */
  public void removeTopic(TopicIF _topic) {
    Topic topic = (Topic)_topic;
    // Check to see if topic is not a member of this topic map
    if (topic.parent != this)
      return;

    // Remove dependencies
    DeletionUtils.removeDependencies(topic);    
    
    // Notify listeners
    fireEvent("TopicMapIF.removeTopic", null, topic);
    // Notify topic listener
    txn.te.removingTopic(topic);
    // Unset topic map property
    topic.setTopicMap(null);
    // Unregister topic with topic map
    topics.remove(topic);
  }
  
  public Collection<AssociationIF> getAssociations() {
    return Collections.unmodifiableSet(assocs);
  }

  /**
   * Adds an association to the set of associations.
   */
  public void addAssociation(AssociationIF _association) {
     
    Association association = (Association)_association;
    // Check to see if association is already a member of this topic map
    if (association.parent == this)
      return;
    // Check if used elsewhere.
    if (association.parent != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    // Notify listeners
    fireEvent("TopicMapIF.addAssociation", association, null);    
    // Set topic map property
    association.setTopicMap(this);
    // Register association with topic map
    assocs.add(association);

    // Make sure roles are added to player's list
    Collection<AssociationRoleIF> roles = association.getRoles();
    synchronized (roles) {
      Iterator<AssociationRoleIF> iter = roles.iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = iter.next();
        Topic player = (Topic) role.getPlayer();
        if (player != null)
          player.addRole(role);      
      }
    }
  }
  
  /**
   * Removes an associations from the set of associations.
   */
  public void removeAssociation(AssociationIF _association) {
    Association association = (Association)_association;
    // Check to see if association is not a member of this topic map
    if (association.parent != this)
      return;
    // Notify listeners
    fireEvent("TopicMapIF.removeAssociation", null, association);

    // Remove players of the association roles
    Collection<AssociationRoleIF> roles = association.roles;
    synchronized (roles) {
      Iterator<AssociationRoleIF> iter = roles.iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = iter.next();
        Topic player = (Topic) role.getPlayer();
        if (player != null)
          player.removeRole(role);      
        //! role.setPlayer(null);
      }
    }
    // Unset topic map property
    association.setTopicMap(null);
    // Unregister association with topic map
    assocs.remove(association);
  }

  public void remove() {
		//! DeletionUtils.removeDependencies(this);
    getStore().delete(true);
  }

  public void clear() {
    DeletionUtils.clear(this);
  }

  public TMObjectIF getObjectById(String object_id) {
    if (object_id == null)
      throw new NullPointerException("null is not a valid argument.");
    return sicache.getObjectById(object_id);
  }

  public TMObjectIF getObjectByItemIdentifier(LocatorIF locator) {
    if (locator == null)
      throw new NullPointerException("null is not a valid argument.");
    return sicache.getObjectByItemIdentifier(locator);
  }

  public TopicIF getTopicBySubjectLocator(LocatorIF locator) { 
    if (locator == null)
      throw new NullPointerException("null is not a valid argument.");
    return sicache.getTopicBySubjectLocator(locator);
  }

  public TopicIF getTopicBySubjectIdentifier(LocatorIF locator) {
    if (locator == null)
      throw new NullPointerException("null is not a valid argument.");
    return sicache.getTopicBySubjectIdentifier(locator);
  }
  
  // -----------------------------------------------------------------------------
  // ReifiableIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getReifier() {
		return reifier;
	}
  
  public void setReifier(TopicIF _reifier) {
		if (_reifier != null) CrossTopicMapException.check(_reifier, this);
    // Notify listeners
		Topic reifier = (Topic)_reifier;
		Topic oldReifier = (Topic)getReifier();
    fireEvent("ReifiableIF.setReifier", reifier, oldReifier);
    this.reifier = reifier;
		if (oldReifier != null) oldReifier.setReified(null);
		if (reifier != null) reifier.setReified(this);
	}

  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------

  protected void fireEvent(String event, Object new_value, Object old_value) {
    processEvent(this, event, new_value, old_value);
  }

  public String toString() {
    return ObjectStrings.toString("basic.TopicMap", (TopicMapIF)this);
  }
  
  // -----------------------------------------------------------------------------
  // EventManagerIF implementation
  // -----------------------------------------------------------------------------
  
  public void addListener(EventListenerIF listener, String event) {
    // Adding itself causes infinite loops.
    if (listener == this)
      return;
    // Initialize event entry
    synchronized (listeners) {
      // Add listener to list of event entry listeners. This is not
      // very elegant, but it works.
      if (!listeners.containsKey(event))
        listeners.put(event, new EventListenerIF[0]);
      Collection<EventListenerIF> event_listeners = new ArrayList<EventListenerIF>(Arrays.asList(listeners.get(event)));
      event_listeners.add(listener);
      listeners.put(event, event_listeners.toArray(new EventListenerIF[0]));
    }
  }

  public void removeListener(EventListenerIF listener, String event) {
    synchronized (listeners) {
      if (listeners.containsKey(event)) {
        // Remove listener from list of event entry listeners. This is
        // not very elegant, but it works.
        Collection<EventListenerIF> event_listeners = new ArrayList<EventListenerIF>(Arrays.asList(listeners.get(event)));
        event_listeners.remove(listener);
        if (event_listeners.isEmpty())
          listeners.remove(event);
        else
          listeners.put(event, event_listeners.toArray(new EventListenerIF[1]));
      }
    }
  }
  
  // -----------------------------------------------------------------------------
  // EventListenerIF
  // -----------------------------------------------------------------------------

  public void processEvent(Object object, String event, Object new_value, Object old_value) {
    // Look up event listeners
    EventListenerIF[] event_listeners = listeners.get(event);
    if (event_listeners != null) {
      // Loop over event listeners
      int size = event_listeners.length;
      for (int i=0; i < size; i++)
        // Notify listener
        (event_listeners[i]).processEvent(object, event, new_value, old_value);
    }
  }
  
}
