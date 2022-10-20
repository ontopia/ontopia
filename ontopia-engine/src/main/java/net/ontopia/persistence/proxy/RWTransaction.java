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

package net.ontopia.persistence.proxy;

import gnu.trove.procedure.TObjectIntProcedure;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.ontopia.topicmaps.impl.rdbms.TMObject;
import net.ontopia.utils.CompactIdentityHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The read-write proxy transaction implementation. 
 */

public class RWTransaction extends AbstractTransaction {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(RWTransaction.class.getName());
  
  // Changes tracked for cache invalidation
  public boolean trackall;
  public final ObjectStates ostates = new ObjectStates();
  
  // Unflushed change sets
  protected Set<PersistentIF> chgcre = new CompactIdentityHashSet<PersistentIF>(5);
  protected Set<PersistentIF> chgdel = new CompactIdentityHashSet<PersistentIF>(5);
  protected Set<PersistentIF> chgdty = new CompactIdentityHashSet<PersistentIF>(5);
  protected Map<IdentityIF, IdentityIF> merges = new LinkedHashMap<IdentityIF, IdentityIF>();
  
  protected boolean flushing;

  public RWTransaction(StorageAccessIF access) {
    super("TX" + access.getId(), access);
    
    // initialize shared data cache
    StorageCacheIF scache = access.getStorage().getStorageCache();
    if (scache != null) {
      trackall = true;
    }
    this.txncache = new RWLocalCache(this, scache);

    // initialize identity map
    this.lrusize = PropertyUtils.getInt(access.getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.identitymap.lru"), 300);
    this.lru = new LRUMap(lrusize);
    
    // instrument transaction cache
    int dinterval = PropertyUtils.getInt(access.getStorage().getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.local.debug"), -1);
    if (dinterval > 0) {
      log.info("Instrumenting local cache.");
      this.txncache = new StatisticsCache("lcache", this.txncache, dinterval);
    }
    
    // Get access registrar from transaction cache (currently the
    // local data cache)
    this.registrar = txncache.getRegistrar();  
    
    // Use IdentityIF object access
    this.oaccess = new PersistentObjectAccess(this);
  }

  @Override
  public boolean isClean() {
    return ostates.isClean();
  }
  
  @Override
  public boolean isReadOnly() {
    return false;
  }
  
  // -----------------------------------------------------------------------------
  // Life cycle
  // -----------------------------------------------------------------------------
  
  @Override
  public void assignIdentity(PersistentIF object) {
    // FIXME: this method is currently being used in TMObject
    // constructor. should consider getting rid of it.
    if (!isactive) {
      throw new TransactionNotActiveException();
    }
    
    if (object._p_getIdentity() != null) {
      throw new OntopiaRuntimeException("Cannot add new identity to object that already has one.");
    }
    
    // create and assign new identity
    IdentityIF identity = access.generateIdentity(object._p_getType());
    object._p_setIdentity(identity);
    
    // consider new object when assigned new identity
    object.setNewObject(true);
  }
  
  @Override
  public void create(PersistentIF object) {
    if (!isactive) {
      throw new TransactionNotActiveException();
    }
    
    // assign identity if object has no identity
    IdentityIF identity = object._p_getIdentity();
    if (identity == null) {      
      assignIdentity(object);
      identity = object._p_getIdentity();
    }
    
    //! System.out.println(">>* " + identity);
    
    // change state and update changeset
    if (object.isTransient()) {
      // add object to the to-be-created list
      chgcre.add(object);
      
      // change state
      object.setPersistent(true);
      
    } else if (object.isDeleted()) {
      //! throw new OntopiaRuntimeException("Cannot recreate deleted object: " + identity + ")");
      // Figure out if object really has been deleted.
      if (!chgdel.remove(object)) {
        // Add object to the to-be-created list
        chgcre.add(object);        
      }
      
      // change state
      object.setPersistent(true);
    } else {
      throw new OntopiaRuntimeException("Object in invalid state: " + identity + ")");
    }
    
    // track all changes
    objectCreated(object);
    
    //! FIXME: No need to register transaction since it should already
    //! be registered. A check for existing transaction could be useful.
    //! object._p_setTransaction(this);
    
    // Register with identity map
    synchronized (identity_map) {
      Object other = identity_map.put(identity, object);
      // Register with LRU cache
      lru.put(identity, object);
      
      // ISSUE: What if identity is already registered?
      if (other != null && !other.equals(object)) {
        log.warn("Created object replaced existing object: " + identity);
      }
    }
    
    //! // Notify access registrar
    //! if (registrar != null) registrar.registerIdentity(identity);
    
    if (log.isDebugEnabled()) {
      log.debug(getId() + ": Object " + identity + " created: " + object._p_getType());
    }
    
    //! System.out.println("CREATING: " + identity + " created: " + object);
    
  }
  
  @Override
  public void delete(PersistentIF object) {    
    if (!isactive) {
      throw new TransactionNotActiveException();
    }
    
    IdentityIF identity = object._p_getIdentity();
    //! System.out.println(">>/ " + identity);
    
    // change state and update changeset
    if (object.isPersistent()) {
      // retrieve all fields from database, so that we can continue using the object
      
      // detach object
      object.detach();
      
      // mark object as deleted
      object.setDeleted(true);
      
      // add object to the to-be-deleted list
      chgcre.remove(object);
      chgdty.remove(object);
      chgdel.add(object);
      
      // track all changes
      objectDeleted(object);
      
    } else {
      throw new OntopiaRuntimeException("Object in invalid state: " + identity + ")");
    }
    
    //! // Unregister with identity map
    //! synchronized (identity_map) {
    //!   // ISSUE: What if identity is not already registered?
    //!   identity_map.remove(identity);
    //!   // Unregister with LRU cache
    //!   lru.remove(identity);
    //! }
    
    if (log.isDebugEnabled()) {
      log.debug(getId() + ": Object " + identity + " deleted.");
    }    
    
  }
  
  // -----------------------------------------------------------------------------
  // Lifecycle
  // -----------------------------------------------------------------------------
  
  @Override
  public synchronized void flush() {
    // Flushing is non-reentrant
    if (flushing) {
      return;
    }
    
    // Complain if the transaction is not active
    if (!isactive) {
      throw new TransactionNotActiveException();
    }
    
    try {
      flushing = true;
      
      // this method is synchronized, because otherwise it is possible for
      // two different threads to flush the same changes at the same time,
      // causing violations of unique ID values and suchlike.
      
      // All dirty [modified, created and deleted]objects have strong
      // references to them.
      
      // Only flush when changes has actually happened.
      if (chgcre.isEmpty() && chgdty.isEmpty() && chgdel.isEmpty()) {
        return;
      }
      
      //! System.out.println("SC: " + chgcre.size());
      //! System.out.println("SD: " + chgdty.size());
      //! System.out.println("SR: " + chgdel.size());
      
      // Store all pending changes in the database
      if (log.isDebugEnabled()) {
        log.debug(getId() + ": Storing transaction changes.");
      }
      
      //! System.out.println("+FLUSHING");
      
      // Create objects marked for creation
      if (!chgcre.isEmpty()) {
        for (PersistentIF object : chgcre) {
          //! System.out.println("FLUSH CREATE: " + object._p_getIdentity());

          // Store object in repository
          access.createObject(oaccess, object);

          // Mark object as stored in database
          object.setInDatabase(true);

          // WARN: Make sure that all non-default fields are marked
          // dirty at this point!!!

          //! // Mark object as being persisted
          //! object._p_setPersistent(true);        
          // Store dirty object fields in repository
          access.storeDirty(oaccess, object);
        }
      }
      
      // Store modified object fields
      if (!chgdty.isEmpty()) {
        for (PersistentIF object : chgdty) {
          // Store dirty object fields in repository
          if (!object.isDeleted()) {
            access.storeDirty(oaccess, object);
          }
        }
      }
      
      // Delete objects marked for deletion
      if (!chgdel.isEmpty()) {
        for (PersistentIF object : chgdel) {

          // Delete object from repository
          access.deleteObject(oaccess, object);

          // Mark object as no longer stored in database
          object.setInDatabase(false);
        }
      }
      
      // Since there were no complaints we can now clear the transaction
      // changes.
      chgcre.clear();
      chgdel.clear();
      chgdty.clear();
      
      // Flush storage access
      access.flush();
      
      //! System.out.println("-FLUSHING");
      
    } finally {
      flushing = false;
    }
    
    if (log.isDebugEnabled()) {
      log.debug(getId() + ": Transaction changes stored.");
    }
  }
  
  // -----------------------------------------------------------------------------
  // Object modification callbacks (called by PersistentIFs)
  // -----------------------------------------------------------------------------
  
  @Override
  public synchronized void objectDirty(PersistentIF object) {
    if (!isactive) {
      throw new TransactionNotActiveException();
    }
    
    if (log.isDebugEnabled()) {
      log.debug(getId() + ": Object dirty " + object._p_getIdentity());
    }
    chgdty.add(object);
    
    // track all changes
    if (trackall) {
      ostates.dirty(object._p_getIdentity());
    }
  }

  @Override
  public void objectRead(IdentityIF identity) {
    if (trackall) {
      ostates.read(identity);
    }
  }

  @Override
  public void objectCreated(PersistentIF object) {
    if (trackall) {
      ostates.created(object._p_getIdentity());
    }
  }

  @Override
  public void objectDeleted(PersistentIF object) {
    if (trackall) {
      ostates.deleted(object._p_getIdentity());
    }
  }

  @Override
  public boolean isObjectClean(IdentityIF identity) {
    return ostates.isClean(identity);
  }

  /**
   * INTERNAL: Called by RDBMSTopicMapStore to notify the transaction
   * of a performed merge.
   * @param source
   * @param target
   */
  public void registerMerge(TMObject source, TMObject target) {
    merges.put(source._p_getIdentity(), target._p_getIdentity());
  }

  /**
   * {@inheritDoc}
   * RWTransaction notifies the added and modified objects of the merge,
   * allowing them to update their fields as needed.
   */
  @Override
  public synchronized void objectMerged(IdentityIF source, IdentityIF target) {
    // let the added and modified objects update their fields as needed
    // todo: removed items shouldn't cause problems ??
    for (Object o : chgcre) {
      if (o instanceof AbstractRWPersistent) {
        ((AbstractRWPersistent) o).syncAfterMerge(source, target);
      }
    }

    for (Object o : chgdty) {
      if (o instanceof AbstractRWPersistent) {
        ((AbstractRWPersistent) o).syncAfterMerge(source, target);
      }
    }
  }

  // -----------------------------------------------------------------------------
  // Transaction boundary callbacks
  // -----------------------------------------------------------------------------
  
  @Override
  protected synchronized void transactionPreCommit() {
    // no-op
  }
  
  @Override
  protected synchronized void transactionPostCommit() {
    // clear change sets
    chgcre.clear();
    chgdty.clear();
    chgdel.clear();

    // sync merges to other transactions
    for (Map.Entry<IdentityIF, IdentityIF> entry : merges.entrySet()) {
      ((RDBMSStorage)access.getStorage()).objectMerged(entry.getKey(), entry.getValue(), this);
    }

    merges.clear();
    
    // invalidate object state
    if (trackall) {
      synchronized (identity_map) {
        synchronized (ostates) {
          // clear objects and caches
          if (ostates.size() > 0) {
            // register eviction process
            txncache.registerEviction();
            try {
              ostates.forEachEntry(new TObjectIntProcedure<IdentityIF>() {
                @Override
                  public boolean execute(IdentityIF identity, int s) {
                    if ((s & ObjectStates.STATE_CREATED) == ObjectStates.STATE_CREATED) {
                      // no-op
                    } else if ((s & ObjectStates.STATE_DELETED) == ObjectStates.STATE_DELETED) {
                      // evict identity from cache
                      txncache.evictIdentity(identity, true);
                    } else if ((s & ObjectStates.STATE_DIRTY) == ObjectStates.STATE_DIRTY) {
                      // evict fields from cache
                      txncache.evictFields(identity, true);
                    }
                    // clear object
                    PersistentIF p = checkIdentityMapNoLRU(identity);
                    if (p != null) {
                      p.clearAll();
                    }
                    return true;
                  }
                });
            } finally {
              // deregister eviction
              txncache.releaseEviction();              
            }
            // clear object states
            ostates.clear();
          }
        }
      }
    }
  }
  
  @Override
  protected synchronized void transactionPreAbort() {
    // no-op
  }
  
  @Override
  protected synchronized void transactionPostAbort() {
    // clear change sets
    chgcre.clear();
    chgdty.clear();
    chgdel.clear();
    merges.clear();
    
    // WARNING: after an abort we really want to get rid of all
    // existing objects, because we no longer know the states of the
    // objects. Objects may have transitioned to a new lifecycle state
    // inside the aborted transaction, so there is know way that one
    // can know if an object is persistent or deleted etc.
    // 
    // Conclusion is that txns that have had
    
    // invalidate object state
    if (trackall) {
      synchronized (identity_map) {
        synchronized (ostates) {
          // clear objects only
          if (ostates.size() > 0) {
            // register eviction process
            txncache.registerEviction();
            try {
              ostates.forEachEntry(new TObjectIntProcedure<IdentityIF>() {
                @Override
                  public boolean execute(IdentityIF identity, int s) {
                    if (((s & ObjectStates.STATE_CREATED) == ObjectStates.STATE_CREATED) ||
                        ((s & ObjectStates.STATE_DELETED) == ObjectStates.STATE_DELETED)) {
                      // drop from identity map
                      PersistentIF p = checkIdentityMapNoLRU(identity);
                      if (p != null) {
                        p.clearAll();
                      }
                    } else {
                      // clear dirty/dirty-read object
                      PersistentIF p = checkIdentityMapNoLRU(identity);
                      if (p != null) {
                        p.clearAll();
                      }
                    }
                    return true;
                  }
                });
            } finally {
              // deregister eviction
              txncache.releaseEviction();              
            }            
            // clear object states
            ostates.clear();
          }
        }
      }
    }
  }

  // -----------------------------------------------------------------------------
  // Prefetching
  // -----------------------------------------------------------------------------

  @Override
  public void prefetch(Class<?> type, int field, boolean traverse, Collection<IdentityIF> identities) {
    // do not prefetch when no shared cache
    if (!trackall) {
      super.prefetch(type, field, traverse, identities);
    }
  }

  @Override
  public void prefetch(Class<?> type, int[] fields, boolean[] traverse, Collection<IdentityIF> identities) {
    // do not prefetch when no shared cache
    if (!trackall) {
      super.prefetch(type, fields, traverse, identities);
    }
  }
  
}
