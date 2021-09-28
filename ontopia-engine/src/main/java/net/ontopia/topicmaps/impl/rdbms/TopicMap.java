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

import java.net.MalformedURLException;
import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.QueryCollection;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: The rdbms topic map implementation.
 */

public class TopicMap extends TMObject implements TopicMapIF {
  
  // ---------------------------------------------------------------------------
  // Persistent property declarations
  // ---------------------------------------------------------------------------

  protected static final int LF_sources = 0;
  protected static final int LF_title = 1;
  protected static final int LF_base_address = 2;
  protected static final int LF_comments = 3;
  protected static final int LF_reifier = 4;

  protected static final String[] fields = {"sources", "title", "base_address", "comments", "reifier"}; // TODO: take these away

  public void detach() {
    detachCollectionField(LF_sources);
    detachField(LF_reifier);
  }
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public static final String CLASS_INDICATOR = "M";

  protected transient RDBMSTopicMapTransaction transaction;  

  public TopicMap() {  
  }
  
  public TopicMap(TransactionIF txn) {
    super(txn);
  }

  public LocatorIF getBaseAddress() {
    String base_address = (String)loadField(LF_base_address);
    if (base_address == null) return null;
    try {
      return new URILocator(base_address);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void setBaseAddress(LocatorIF baseAddress) {
    String base_address = (baseAddress == null ? null : baseAddress.getAddress());
    // Notify transaction
    valueChanged(LF_base_address, base_address, true);
  }

  public String getTitle() {
    return (String)loadField(LF_title);
  }

  public void setTitle(String title) {
    // Notify listeners
    //! fireEvent("TopicMapIF.setTitle", title, loadField(LF_title));
    // Notify transaction
    valueChanged(LF_title, title, true);
  }

  public String getComments() {
    return (String)loadField(LF_comments);
  }

  public void setComments(String comments) {
    // Notify listeners
    //! fireEvent("TopicMapIF.setComments", comments, loadField(LF_comments));
    // Notify transaction
    valueChanged(LF_comments, comments, true);
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public String getClassIndicator() {
    return CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : CLASS_INDICATOR + id.getKey(0));
  }

  public TopicMapIF getTopicMap() {
    return this;
  }

  void setTopicMap(TopicMapIF topicmap) {
    // Shouldn't do anything here.
  }

  // ---------------------------------------------------------------------------
  // TopicMapIF implementation
  // ---------------------------------------------------------------------------

  public TopicMapStoreIF getStore() {
    return transaction.getStore();
  }

  public TopicMapTransactionIF getTransaction() {
    return transaction;
  }

  public TopicMapBuilderIF getBuilder() {
    return getTransaction().getBuilder();
  }

  public Object getIndex(String name) {
    return getTransaction().getIndexManager().getIndex(name);
  }

  public void setTransaction(RDBMSTopicMapTransaction transaction) {
    // The transaction property can only be set once
    if (this.transaction != null)
      throw new OntopiaRuntimeException("Transaction can only be set once.");
    
    this.transaction = transaction;
  }
  
  public Collection<TopicIF> getTopics() {
    Object[] params = new Object[] { getTopicMap() };
    return new QueryCollection<TopicIF>(txn, "TopicMap.getTopics_size", params,
                                        "TopicMap.getTopics_iterator", params);
  }

  /**
   * Adds a topic to the set of topics.
   */
  void addTopic(TopicIF topic) {
    // Check to see if topic is already a member of this topic map
    if (topic.getTopicMap() == this)
      return;
    // Check if used elsewhere.
    if (topic.getTopicMap() != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    // Notify listeners
    fireEvent(TopicMapIF.EVENT_ADD_TOPIC, topic, null);
    // Set topic map property
    ((Topic)topic).setTopicMap(this);
    // Notify topic listener
    transaction.te.addedTopic(topic);
  }

  /**
   * Removes a topic from the set of topics.
   */
  void removeTopic(TopicIF topic) {
    // Check to see if topic is not a member of this topic map
    if (topic.getTopicMap() != this)
      return;

    // Remove dependencies
    DeletionUtils.removeDependencies(topic);    

    // Notify listeners
    fireEvent(TopicMapIF.EVENT_REMOVE_TOPIC, null, topic);    
    // Notify topic listener
    transaction.te.removingTopic(topic);
    // Unset topic map property
    ((Topic)topic).setTopicMap(null);
  }
  
  public Collection<AssociationIF> getAssociations() {
    Object[] params = new Object[] { getTopicMap() };
    return new QueryCollection<AssociationIF>
      (txn, "TopicMap.getAssociations_size", params,
       "TopicMap.getAssociations_iterator", params);
    
  }

  /**
   * Adds an association to the set of associations.
   */
  void addAssociation(AssociationIF association) {
    // Check to see if association is already a member of this topic map
    if (association.getTopicMap() == this)
      return;
    // Check if used elsewhere.
    if (association.getTopicMap() != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    // Notify listeners
    fireEvent(TopicMapIF.EVENT_ADD_ASSOCIATION, association, null);    
    // Set topic map property
    ((Association)association).setTopicMap(this);
  }
  
  /**
   * Removes an associations from the set of associations.
   */
  void removeAssociation(AssociationIF association) {
    // Check to see if association is not a member of this topic map
    if (association.getTopicMap() != this)
      return;
    // Notify listeners
    fireEvent(TopicMapIF.EVENT_REMOVE_ASSOCIATION, null, association);
    // Unset topic map property
    ((Association)association).setTopicMap(null);
  }

  public TMObjectIF getObjectById(String object_id) {
    if (object_id == null)
      throw new NullPointerException("null is not a valid argument.");

    // Cut off the indicator character and lookup identity in the database.
    try {
      long numid;
      try {
        numid = Long.parseLong(object_id.substring(1), 10);
      } catch (NumberFormatException e) {
        return null; // if not a valid ID no object will have it... :)
      }
      
      TMObjectIF result = null;
      switch ( object_id.charAt(0) ) {
      case 'T': {
        // Lookup topic
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(Topic.class, numid));
        break;
      }
      case 'A': {
        // Lookup association
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(Association.class, numid));
        break;
      }
      case 'O': {
        // Lookup occurrence
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(Occurrence.class, numid));
        break;
      }
      case 'B': {
        // Lookup name
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(TopicName.class, numid));
        break;
      }
      case 'N': {
        // Lookup variant name
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(VariantName.class, numid));
        break;
      }
      case 'R': {
        // Lookup association role
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(AssociationRole.class, numid));
        break;
      }
      case 'M': {
        // Lookup topic map (only this one can match)
        if (object_id.equals(getObjectId()))
          return this;
      }
      default:
        // No object was found
        return null;
      }
      
      // return object if it is part of this topic map
      return ((result == null || result.getTopicMap() != this) ? null : result);

    } catch (IndexOutOfBoundsException e) {
      return null;
    } catch (OntopiaRuntimeException e) {
      return null;
    }
  }

  public void remove() {
    getStore().delete(true);
  }

  public void clear() {
    DeletionUtils.clear(this);
  }

  // ---------------------------------------------------------------------------
  // Subject identity cache
  // ---------------------------------------------------------------------------

  public TMObjectIF getObjectByItemIdentifier(LocatorIF locator) {
    return transaction.getObjectByItemIdentifier(locator);
  }

  public TopicIF getTopicBySubjectLocator(LocatorIF locator) {
    return transaction.getTopicBySubjectLocator(locator);
  }

  public TopicIF getTopicBySubjectIdentifier(LocatorIF locator) {
    return transaction.getTopicBySubjectIdentifier(locator);
  }

  // ---------------------------------------------------------------------------
  // Role type cache
  // ---------------------------------------------------------------------------

  public Collection<AssociationRoleIF> getRolesByType(TopicIF player, TopicIF rtype) {
    return transaction.getRolesByType(player, rtype);
  }

  // ---------------------------------------------------------------------------
  // Role type and association type cache
  // ---------------------------------------------------------------------------


  public Collection<AssociationRoleIF> getRolesByType(TopicIF player, TopicIF rtype, TopicIF atype) {
    return transaction.getRolesByType(player, rtype, atype);
  }

  // ---------------------------------------------------------------------------
  // Optimized shortcuts
  // ---------------------------------------------------------------------------
  
  public Collection<OccurrenceIF> getOccurrencesByType(TopicIF topic, TopicIF type) {
    return transaction.getOccurrencesByType(topic, type);
  }
  
  public Collection<TopicNameIF> getTopicNamesByType(TopicIF topic, TopicIF type) {
    return transaction.getTopicNamesByType(topic, type);
  }

  public Collection<AssociationIF> getAssocations(TopicIF topic) {
    return transaction.getAssocations(topic);
  }

  public Collection<AssociationIF> getAssociationsByType(TopicIF topic, TopicIF type) {
    return transaction.getAssociationsByType(topic, type);
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getReifier() {
    return (TopicIF)loadField(LF_reifier);
  }
  
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null) CrossTopicMapException.check(_reifier, this);
    // Notify listeners
    Topic reifier = (Topic)_reifier;
    Topic oldReifier = (Topic)getReifier();
    fireEvent(ReifiableIF.EVENT_SET_REIFIER, reifier, oldReifier);
    valueChanged(LF_reifier, reifier, true);
    if (oldReifier != null) oldReifier.setReified(null);
    if (reifier != null) reifier.setReified(this);
  }
  
  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  public String toString() {
    return ObjectStrings.toString("rdbms.TopicMap", (TopicMapIF)this);
  }

  @Override
  public void syncAfterMerge(IdentityIF source, IdentityIF target) {
    syncFieldsAfterMerge(source, target, LF_reifier);
  }
}
