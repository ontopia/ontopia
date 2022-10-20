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

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A transactional storage cache implementation. The cache
 * uses the transaction to lookup objects and relies on the fact that
 * PersistentIFs can store their own data.
 */

public abstract class AbstractLocalCache implements StorageCacheIF, AccessRegistrarIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(AbstractLocalCache.class.getName());

  protected AbstractTransaction txn;
  protected StorageCacheIF pcache;
  protected AccessRegistrarIF pregistrar;
  protected TicketIF ticket;
  
  AbstractLocalCache(AbstractTransaction txn, StorageCacheIF pcache) {
    this.txn = txn;
    this.pcache = pcache;
    if (this.pcache != null) {
      this.pregistrar = this.pcache.getRegistrar();
    } else {
      this.ticket = new TicketIF() {
          @Override
          public boolean isValid() {
            return true;
          }
        };
    }
  }

  // -----------------------------------------------------------------------------
  // StorageCacheIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public AccessRegistrarIF getRegistrar() {
    return this;
  }
  
  @Override
  public void close() {
  }

  @Override
  public abstract boolean exists(StorageAccessIF access, IdentityIF identity);
  
  @Override
  public abstract Object getValue(StorageAccessIF access, IdentityIF identity, int field);

  @Override
  public boolean isObjectLoaded(IdentityIF identity) {
    if (pcache != null) {
      return pcache.isObjectLoaded(identity);
    }

    return false;
  }

  @Override
  public boolean isFieldLoaded(IdentityIF identity, int field) {
    if (pcache != null) {
      return pcache.isFieldLoaded(identity, field);
    }
    
    return false;
  }

  // -----------------------------------------------------------------------------
  // eviction
  // -----------------------------------------------------------------------------

  @Override
  public void registerEviction() {
    if (pcache != null) {
      pcache.registerEviction();
    }
  }
  
  @Override
  public void releaseEviction() {
    if (pcache != null) {
      pcache.releaseEviction();
    }
  }

  @Override
  public void evictIdentity(IdentityIF identity, boolean notifyCluster) {
    if (pcache != null) {
      pcache.evictIdentity(identity, notifyCluster);
    }
  }

  @Override
  public void evictFields(IdentityIF identity, boolean notifyCluster) {
    if (pcache != null) {
      pcache.evictFields(identity, notifyCluster);
    }
  }

  @Override
  public void evictField(IdentityIF identity, int field, boolean notifyCluster) {
    if (pcache != null) {
      pcache.evictField(identity, field, notifyCluster);
    }
  }  

  @Override
  public void clear(boolean notifyCluster) {
    if (pcache != null) {
      pcache.clear(notifyCluster);
    }
  }

  // -----------------------------------------------------------------------------
  // prefetch
  // -----------------------------------------------------------------------------

  // ISSUE: prefetch locally if no parent cache?

  @Override
  public int prefetch(StorageAccessIF access, Class<?> type, int field, int nextField, boolean traverse, Collection<IdentityIF> identities) {
    // WARNING: dirty objects should never be handed over to shared cache
    if (pcache != null) {
      return pcache.prefetch(access, type, field, nextField, traverse, identities);
    }
    return 0;
  }
  
  // -----------------------------------------------------------------------------
  // AccessRegistrarIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public IdentityIF createIdentity(Class<?> type, long key) {
    return new LongIdentity(type, key);
  }

  @Override
  public IdentityIF createIdentity(Class<?> type, Object key) {
    return new AtomicIdentity(type, key);
  }
  
  @Override
  public IdentityIF createIdentity(Class<?> type, Object[] keys) {
    return new Identity(type, keys);
  }

  @Override
  public TicketIF getTicket() {
    if (pregistrar == null) {
      return ticket;
    } else {
      return pregistrar.getTicket();
    }
  }
  
  @Override
  public void registerIdentity(TicketIF ticket, IdentityIF identity) {
    if (log.isDebugEnabled()) {
      log.debug("Registering identity " + identity);
    }

    // notify parent cache if no txn changes
    if (pregistrar != null && txn.isObjectClean(identity)) {
      pregistrar.registerIdentity(ticket, identity);
    }

    // make sure txn holds an object instance
    txn.checkIdentityMapAndCreateInstance(identity);
  }

  @Override
  public void registerField(TicketIF ticket, IdentityIF identity, int field, Object value) {
    if (log.isDebugEnabled()) {
      log.debug("Registering " + identity + " field " + field + "=" + value);
    }      

    // notify parent cache if no txn changes
    if (pregistrar != null && txn.isObjectClean(identity)) {
      pregistrar.registerField(ticket, identity, field, value);
    }
    
    // make sure txn holds an object instance
    txn.checkIdentityMapAndCreateInstance(identity);
  }
  
}
