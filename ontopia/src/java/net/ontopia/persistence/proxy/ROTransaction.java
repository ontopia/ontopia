
// $Id: ROTransaction.java,v 1.6 2007/09/17 08:28:02 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import gnu.trove.TObjectIntProcedure;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.ontopia.utils.CompactIdentityHashSet;
import net.ontopia.utils.LookupIndexIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.SoftValueHashMapIndex;

import org.apache.log4j.Logger;
import org.apache.commons.collections.map.LRUMap;

/**
 * INTERNAL: The read-only proxy transaction implementation. 
 */

public class ROTransaction extends AbstractTransaction {
  
  // Define a logging category.
  static Logger log = Logger.getLogger(ROTransaction.class.getName());
  
  public ROTransaction(StorageAccessIF access) {
    super("TX" + access.getId(), access);
    
    // initialize shared data cache
    StorageCacheIF scache = access.getStorage().getStorageCache();
    this.txncache = new ROLocalCache(this, scache);

    // initialize identity map
    this.identity_map = new SoftValueHashMapIndex();
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

  public boolean isClean() {
    return true;
  }
  
  public boolean isReadOnly() {
    return true;
  }
  
  // -----------------------------------------------------------------------------
  // Life cycle
  // -----------------------------------------------------------------------------
  
  public void assignIdentity(PersistentIF object) {
    if (!isactive) throw new TransactionNotActiveException();
    throw new ReadOnlyTransactionException();
  }
  
  public void create(PersistentIF object) {
    if (!isactive) throw new TransactionNotActiveException();
    throw new ReadOnlyTransactionException();
  }
  
  public void delete(PersistentIF object) {    
    if (!isactive) throw new TransactionNotActiveException();
    throw new ReadOnlyTransactionException();
  }
  
  // -----------------------------------------------------------------------------
  // Lifecycle
  // -----------------------------------------------------------------------------
  
  public void flush() {
    // no-op
  }
  
  // -----------------------------------------------------------------------------
  // Object modification callbacks (called by PersistentIFs)
  // -----------------------------------------------------------------------------
  
  public void objectDirty(PersistentIF object) {
    throw new ReadOnlyTransactionException();
  }

  public void objectRead(IdentityIF identity) {
  }

  public void objectCreated(PersistentIF object) {
    throw new ReadOnlyTransactionException();
  }

  public void objectDeleted(PersistentIF object) {
    throw new ReadOnlyTransactionException();
  }

  public boolean isObjectClean(IdentityIF identity) {
    return true;
  }

  // -----------------------------------------------------------------------------
  // Transaction boundary callbacks
  // -----------------------------------------------------------------------------
  
  protected void transactionPreCommit() {
    // no-op
  }
  
  protected void transactionPostCommit() {
    // no-op
  }
  
  protected void transactionPreAbort() {
    // no-op
  }
  
  protected void transactionPostAbort() {
    // no-op
  }
  
}
