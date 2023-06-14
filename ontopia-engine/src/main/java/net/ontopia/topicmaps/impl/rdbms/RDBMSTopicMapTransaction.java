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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.PersistentIF;
import net.ontopia.persistence.proxy.QueryCollection;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.core.TransactionNotActiveException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.impl.rdbms.index.IndexManager;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapTransaction;
import net.ontopia.topicmaps.impl.utils.EventListenerIF;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.topicmaps.impl.utils.TopicModificationManager;
import net.ontopia.utils.CollectionFactory;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.OntopiaUnsupportedException;

/**
 * INTERNAL: The rdbms topic map transaction implementation.<p>
 */

public class RDBMSTopicMapTransaction extends AbstractTopicMapTransaction
  implements EventManagerIF {
  protected TransactionIF txn;
  protected boolean readonly; 

  protected long actual_id;

  protected SubjectIdentityCache sicache;
  protected RoleTypeCache rtcache;
  protected RoleTypeAssocTypeCache rtatcache;
  protected Map listeners;  

  protected ObjectTreeManager otree;
  protected TopicModificationManager topicmods;
  protected TopicEvents te;
  
  RDBMSTopicMapTransaction(RDBMSTopicMapStore store, long topicmap_id, boolean readonly) {
    this.store = store;
    this.readonly = readonly;

    // Begin new transaction
    this.txn = store.getStorage().createTransaction(readonly);
    this.txn.begin();

    // Create or look up topic map object
    if (topicmap_id > 0) {
      // Get hold of topic map object
      topicmap = (TopicMapIF)txn.getObject(txn.getAccessRegistrar().createIdentity(TopicMap.class, topicmap_id));
      if (topicmap == null) {
        throw new OntopiaRuntimeException("Topic map with id '" + topicmap_id + " not found.");
      }
    }
    else {
      if (readonly) {
        throw new ReadOnlyException();
      } else {
        // Create new topic map object and register with database
        topicmap = new TopicMap(txn);
        txn.create((PersistentIF)topicmap);
      }
    }
    
    IdentityIF identity = ((PersistentIF)topicmap)._p_getIdentity();
    this.actual_id = ((Long)(identity.getKey(0))).longValue();
    
    // Register store with topic map
    if (readonly) {
      ((ReadOnlyTopicMap)topicmap).setTransaction(this);
    } else {
      ((TopicMap)topicmap).setTransaction(this);
    }
    
    // Activate transaction (note: must be activated at this point, because of dependencies)
    this.active = true;    
    
    // Initialize collection factory
    this.cfactory = new CollectionFactory();

    // Initialize listeners
    this.listeners = cfactory.makeSmallMap();

    // Register object tree event listener with store event manager
    this.otree = new ObjectTreeManager(this, cfactory);
    this.topicmods = new TopicModificationManager(this, cfactory);
    this.te = new TopicEvents(store);
    this.te.registerListeners(this);
    this.topicmods.addListener(this.te, TopicIF.EVENT_MODIFIED);
    
    // QueryCache: subject identity cache
    this.sicache = new SubjectIdentityCache(this, cfactory);
    this.sicache.registerListeners(this, otree);

    // QueryCache: role type cache
    this.rtcache = new RoleTypeCache(this, this, otree);

    // QueryCache: role type assoc type cache
    this.rtatcache = new RoleTypeAssocTypeCache(this, this, otree);

    // Create new index manager
    this.imanager = new IndexManager(this, cfactory);

    // Create topic map builder
    this.builder = new TopicMapBuilder(txn, topicmap);
  }

  public long getActualId() {
    return actual_id;
  }

  public ObjectTreeManager getObjectTreeManager() {
    return otree;
  }
  
  @Override
  public void commit() {
    if (!readonly) {
      synchronized (this) {
        super.commit();
        
        // commit proxy transaction
        txn.commit();
        
        // reset query caches
        sicache.commit();
        rtcache.commit();
        rtatcache.commit();

        // notify cluster
        txn.getStorageAccess().getStorage().notifyCluster();
        
        // commmit listeners
        processEvent(this, TopicMapTransactionIF.EVENT_COMMIT, null, null);
      }
    } else {
      txn.commit();
    }
  }

  @Override
  public void abort() {
    if (!readonly) {
      synchronized (this) {
        super.abort();

        // abort listeners
        processEvent(this, TopicMapTransactionIF.EVENT_ABORT, null, null);
      }
    }
  }

  @Override
  public void abort(boolean invalidate) {
    if (!readonly || invalidate) {
      synchronized (this) {
        super.abort(invalidate);
        
        // Invalidate transaction
        invalid = (invalid || invalidate);
        
        // Abort proxy transaction
        if (txn.isActive()) {
          txn.abort();
        }
        
        if (invalidate) {
          if (active) {
            // Close proxy transaction
            txn.close();
            
            // Deactivate topic map transaction
            active = false;
          }
        } else {
          // Reset query caches
          sicache.abort();
          rtcache.abort();
          rtatcache.abort();
        }
      }
    } else if (readonly) {
      txn.abort();
    }
  }

  @Override
  public boolean validate() {
    // if transaction has been aborted the store is invalid
    if (invalid) {
      return false;
    }
    // check proxy transaction
    return txn.validate();    
  }

  @Override
  public TopicMapTransactionIF createNested() {
    // Nested transactions are not supported
    throw new OntopiaUnsupportedException("Nested transactions not supported.");
  }

  /**
   * INTERNAL: Returns the proxy transaction used by the topic map
   * transaction.
   */
  public TransactionIF getTransaction() {
    if (txn == null) {
      throw new TransactionNotActiveException();
    }
    return txn;
  }
  
  // ---------------------------------------------------------------------------
  // EventManagerIF implementation
  // ---------------------------------------------------------------------------
  
  @Override
  public void addListener(EventListenerIF listener, String event) {
    // Adding itself causes infinite loops.
    if (listener == this) {
      return;
    }
    // Initialize event entry
    synchronized (listeners) {
      // Add listener to list of event entry listeners. This is not
      // very elegant, but it works.
      if (!listeners.containsKey(event)) {
        listeners.put(event, new Object[0]);
      }
      Collection event_listeners = new ArrayList(Arrays.asList((Object[])listeners.get(event)));
      event_listeners.add(listener);
      listeners.put(event, event_listeners.toArray());      
    }
  }

  @Override
  public void removeListener(EventListenerIF listener, String event) {
    synchronized (listeners) {
      if (listeners.containsKey(event)) {
        // Remove listener from list of event entry listeners. This is
        // not very elegant, but it works.
        Collection event_listeners = new ArrayList(Arrays.asList((Object[])listeners.get(event)));
        event_listeners.remove(listener);
        if (event_listeners.isEmpty()) {
          listeners.remove(event);
        } else {
          listeners.put(event, event_listeners.toArray());
        }
      }
    }
  }

  @Override
  public void processEvent(Object object, String event, Object new_value, Object old_value) {
    // Look up event listeners
    Object[] event_listeners = (Object[])listeners.get(event);
    if (event_listeners != null) {
      // Loop over event listeners
      int size = event_listeners.length;
      for (int i=0; i < size; i++) {
        // Notify listener
        ((EventListenerIF)event_listeners[i]).processEvent(object, event, new_value, old_value);
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Prefetch: roles by type and association type
  // ---------------------------------------------------------------------------

  public void prefetchRolesByType(Collection players, 
                                  TopicIF rtype, TopicIF atype) {
    this.rtatcache.prefetchRolesByType(players, rtype, atype);
  }

  // ---------------------------------------------------------------------------
  // Subject identity cache
  // ---------------------------------------------------------------------------

  public TMObjectIF getObjectByItemIdentifier(LocatorIF locator) {
    Objects.requireNonNull(locator, "null is not a valid argument.");

    // Get from subject identity cache
    return sicache.getObjectByItemIdentifier(locator);
  }

  public TopicIF getTopicBySubjectLocator(LocatorIF locator) {
    Objects.requireNonNull(locator, "null is not a valid argument.");
          
    // Get from subject identity cache
    return sicache.getTopicBySubjectLocator(locator);
  }
  
  public TopicIF getTopicBySubjectIdentifier(LocatorIF locator) {
    Objects.requireNonNull(locator, "null is not a valid argument.");
          
    // Get from subject identity cache
    return sicache.getTopicBySubjectIdentifier(locator);
  }

  // ---------------------------------------------------------------------------
  // Role type cache
  // ---------------------------------------------------------------------------

  public Collection<AssociationRoleIF> getRolesByType(TopicIF player, TopicIF rtype) {
    return rtcache.getRolesByType(player, rtype);
  }

  // ---------------------------------------------------------------------------
  // Role type and association type cache
  // ---------------------------------------------------------------------------

  public Collection<AssociationRoleIF> getRolesByType(TopicIF player, TopicIF rtype, TopicIF atype) {
    return rtatcache.getRolesByType(player, rtype, atype);
  }

  // ---------------------------------------------------------------------------
  // Optimized shortcuts
  // ---------------------------------------------------------------------------

  public Collection<OccurrenceIF> getOccurrencesByType(TopicIF topic, TopicIF type) {
    return new QueryCollection<OccurrenceIF>(txn, 
            "TopicIF.getOccurrencesByType_size", new Object[] {getTopicMap(), topic, type}, 
            "TopicIF.getOccurrencesByType", new Object[] {getTopicMap(), topic, type});
  }
  
  public Collection<TopicNameIF> getTopicNamesByType(TopicIF topic, TopicIF type) {
    return new QueryCollection<TopicNameIF>(txn, 
            "TopicIF.getTopicNamesByType_size", new Object[] {getTopicMap(), topic, type}, 
            "TopicIF.getTopicNamesByType", new Object[] {getTopicMap(), topic, type});
  }

  public Collection<AssociationIF> getAssocations(TopicIF topic) {
    return new QueryCollection<AssociationIF>(txn, 
            "TopicIF.getAssociations_size", new Object[] {getTopicMap(), getTopicMap(), topic}, 
            "TopicIF.getAssociations", new Object[] {getTopicMap(), getTopicMap(), topic});
  }

  public Collection<AssociationIF> getAssociationsByType(TopicIF topic, TopicIF type) {
    return new QueryCollection<AssociationIF>(txn, 
            "TopicIF.getAssociationsByType_size", new Object[] {getTopicMap(), type, getTopicMap(), topic}, 
            "TopicIF.getAssociationsByType", new Object[] {getTopicMap(), type, getTopicMap(), topic});
  }

  @Override
  public String toString() {
    return "[rdbms.Transaction, " + actual_id + ", " + readonly + "]";
  }

}
