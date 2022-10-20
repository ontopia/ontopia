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

import net.ontopia.utils.PropertyUtils;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The read-only proxy transaction implementation. 
 */

public class ROTransaction extends AbstractTransaction {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(ROTransaction.class.getName());
  
  public ROTransaction(StorageAccessIF access) {
    super("TX" + access.getId(), access);
    
    // initialize shared data cache
    StorageCacheIF scache = access.getStorage().getStorageCache();
    this.txncache = new ROLocalCache(this, scache);

    // initialize identity map
    this.lrusize = PropertyUtils.getInt(access.getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.shared.identitymap.lru"), 5000);
    this.lru = new LRUMap(this.lrusize);

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
    return true;
  }
  
  @Override
  public boolean isReadOnly() {
    return true;
  }
  
  // -----------------------------------------------------------------------------
  // Life cycle
  // -----------------------------------------------------------------------------
  
  @Override
  public void assignIdentity(PersistentIF object) {
    if (!isactive) {
      throw new TransactionNotActiveException();
    }
    throw new ReadOnlyTransactionException();
  }
  
  @Override
  public void create(PersistentIF object) {
    if (!isactive) {
      throw new TransactionNotActiveException();
    }
    throw new ReadOnlyTransactionException();
  }
  
  @Override
  public void delete(PersistentIF object) {    
    if (!isactive) {
      throw new TransactionNotActiveException();
    }
    throw new ReadOnlyTransactionException();
  }
  
  // -----------------------------------------------------------------------------
  // Lifecycle
  // -----------------------------------------------------------------------------
  
  @Override
  public void flush() {
    // no-op
  }
  
  // -----------------------------------------------------------------------------
  // Object modification callbacks (called by PersistentIFs)
  // -----------------------------------------------------------------------------
  
  @Override
  public void objectDirty(PersistentIF object) {
    throw new ReadOnlyTransactionException();
  }

  @Override
  public void objectRead(IdentityIF identity) {
    // no-op
  }

  @Override
  public void objectCreated(PersistentIF object) {
    throw new ReadOnlyTransactionException();
  }

  @Override
  public void objectDeleted(PersistentIF object) {
    throw new ReadOnlyTransactionException();
  }

  @Override
  public boolean isObjectClean(IdentityIF identity) {
    return true;
  }

  // -----------------------------------------------------------------------------
  // Transaction boundary callbacks
  // -----------------------------------------------------------------------------
  
  @Override
  protected void transactionPreCommit() {
    // no-op
  }
  
  @Override
  protected void transactionPostCommit() {
    // no-op
  }
  
  @Override
  protected void transactionPreAbort() {
    // no-op
  }
  
  @Override
  protected void transactionPostAbort() {
    // no-op
  }
  
}
