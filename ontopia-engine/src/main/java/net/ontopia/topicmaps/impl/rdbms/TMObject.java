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

import java.util.Collection;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.AbstractRWPersistent;
import net.ontopia.persistence.proxy.IdentityNotFoundException;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;

/**
 * INTERNAL: 
 */
public abstract class TMObject extends AbstractRWPersistent
  implements TMObjectIF {

  // ---------------------------------------------------------------------------
  // Field declarations
  // ---------------------------------------------------------------------------

  // Implementation specific field indexes
  protected static final int LF_sources = 0;
  protected static final int LF_topicmap = 1; // Note TopicMapIF.
  
  // static String[] fields; // Defined in concrete class
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public static String CLASS_INDICATOR;

  public TMObject() {
  }
  
  public TMObject(TransactionIF txn) {
    super(txn);

    // create object identity
    txn.assignIdentity(this);
  }
  
  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  /**
   * INTERNAL: Returns the token that can be used to indicate the
   * class of this instance. This indicator is currently only used by
   * source locators.
   */
  public abstract String getClassIndicator();

  /**
   * INTERNAL: Called when the transaction to which the object belongs
   * has changed.
   */
  protected void transactionChanged(TopicMap topicmap) {
    // Note: this means that the object was attached or detached from
    // the topic map transaction.
    if (topicmap != null) {
      // Register with same transaction as topic map
      TransactionIF ptxn = topicmap._p_getTransaction();
      // FIXME: can the transaction ever be null in this case?
      if (ptxn != null) {
        ptxn.create(this);
      }
    } else {
      // Delete from repository
      if (isPersistent()) {
        txn.delete(this);
      }
    }
  }
  
  protected long getLongId() {
    return ((Long)id.getKey(0)).longValue();
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public abstract String getObjectId();

  @Override
  public boolean isReadOnly() {
    return txn.isReadOnly();
  }

  @Override
  public TopicMapIF getTopicMap() {
    try {
      return this.<TopicMapIF>loadField(LF_topicmap);
    } catch (IdentityNotFoundException e) {
      // object has been deleted by somebody else, so return null
      return null;
    }
  }

  @Override
  public Collection<LocatorIF> getItemIdentifiers() {
    return this.<LocatorIF>loadCollectionField(LF_sources);
  }

  @Override
  public void addItemIdentifier(LocatorIF source_locator)
    throws ConstraintViolationException {
    Objects.requireNonNull(source_locator, "null is not a valid argument.");
    // Notify topic map
    if (getTopicMap() == null) {
      throw new ConstraintViolationException("Cannot modify item identifiers when object isn't attached to a topic map.");
    }

    // Check to see if the item identifier is already a item identifier
    // of this topic.    
    Collection<LocatorIF> sources = this.<LocatorIF>loadCollectionField(LF_sources);
    if (sources.contains(source_locator)) {
      return;
    }    

    // Note: Need to morph it into item identifier to ensure that it is
    // correctly handled by the mapping.

    // FIXME: Since this is an aggregate field, the O/R mapper should
    // be able to figure this out itself by looking at the field index
    // and the interface implemented by the value.
    SourceLocator _source_locator = new SourceLocator(source_locator);
    _source_locator._setTMObject(this.getLongId());
    _source_locator._setClassIndicator(this.getClassIndicator());
    _source_locator._setTopicMap(((TopicMap)getTopicMap()).getLongId());
    
    // Notify listeners
    fireEvent(TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, _source_locator, null);
    // Notify transaction
    valueAdded(LF_sources, _source_locator, true);
  }

  @Override
  public void removeItemIdentifier(LocatorIF source_locator) {
    Objects.requireNonNull(source_locator, "null is not a valid argument.");
    // Notify topic map
    if (getTopicMap() == null) {
      throw new ConstraintViolationException("Cannot modify item identifiers " +
                                 "when object isn't attached to a topic map.");
    }
    
    // Check to see if item identifier is a item identifier of this topic.
    Collection<LocatorIF> sources = this.<LocatorIF>loadCollectionField(LF_sources);
    if (!sources.contains(source_locator)) {
      return;
    }

    // Note: Need to morph it into item identifier to ensure that it is
    // correctly handled by the mapping.
    SourceLocator _source_locator = new SourceLocator(source_locator);
    _source_locator._setTMObject(this.getLongId());
    _source_locator._setClassIndicator(this.getClassIndicator());
    _source_locator._setTopicMap(((TopicMap)getTopicMap()).getLongId());
    
    // Notify listeners
    fireEvent(TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, null, _source_locator);
    // Notify transaction
    valueRemoved(LF_sources, _source_locator, true);
  }

  // ---------------------------------------------------------------------------
  // Event handling
  // ---------------------------------------------------------------------------

  /**
   * INTERNAL: Fires an event, so that listeners can be informed about
   * the event. This method is typically called when the object is
   * modified.
   */
  protected void fireEvent(String event, Object new_value, Object old_value) {
    // Note: The object should already have been materialized at this
    // point, because it is about to be modified.
    TopicMapIF topicmap = getTopicMap();
    if (topicmap == null) {
      return;
    }
    RDBMSTopicMapStore store = (RDBMSTopicMapStore)topicmap.getStore();
    EventManagerIF emanager = (EventManagerIF)store.getTransaction();
    // System.out.println("->Object: " + this + " event: " + event + " new: " + new_value + " old:" + old_value);
    emanager.processEvent(this, event, new_value, old_value);
  }
  
}
