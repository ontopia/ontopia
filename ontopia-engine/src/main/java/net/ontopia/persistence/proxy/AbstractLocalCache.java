
// $Id: AbstractLocalCache.java,v 1.7 2007/09/27 12:20:22 geir.gronmo Exp $

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
  static Logger log = LoggerFactory.getLogger(AbstractLocalCache.class.getName());

  protected AbstractTransaction txn;
  protected StorageCacheIF pcache;
  protected AccessRegistrarIF pregistrar;
  protected TicketIF ticket;
  
  AbstractLocalCache(AbstractTransaction txn, StorageCacheIF pcache) {
    this.txn = txn;
    this.pcache = pcache;
    if (this.pcache != null)
      this.pregistrar = this.pcache.getRegistrar();
    else {
      this.ticket = new TicketIF() {
          public boolean isValid() {
            return true;
          }
        };
    }
  }

  // -----------------------------------------------------------------------------
  // StorageCacheIF implementation
  // -----------------------------------------------------------------------------

  public AccessRegistrarIF getRegistrar() {
    return this;
  }
  
  public void close() {
  }

  public abstract boolean exists(StorageAccessIF access, IdentityIF identity);
  
  public abstract Object getValue(StorageAccessIF access, IdentityIF identity, int field);

  public boolean isObjectLoaded(IdentityIF identity) {
    if (pcache != null) 
      return pcache.isObjectLoaded(identity);

    return false;
  }

  public boolean isFieldLoaded(IdentityIF identity, int field) {
    if (pcache != null) 
      return pcache.isFieldLoaded(identity, field);
    
    return false;
  }

  // -----------------------------------------------------------------------------
  // eviction
  // -----------------------------------------------------------------------------

  public void registerEviction() {
    if (pcache != null) pcache.registerEviction();
  }
  
  public void releaseEviction() {
    if (pcache != null) pcache.releaseEviction();
  }

  public void evictIdentity(IdentityIF identity, boolean notifyCluster) {
    if (pcache != null) pcache.evictIdentity(identity, notifyCluster);
  }

  public void evictFields(IdentityIF identity, boolean notifyCluster) {
    if (pcache != null) pcache.evictFields(identity, notifyCluster);
  }

  public void evictField(IdentityIF identity, int field, boolean notifyCluster) {
    if (pcache != null) pcache.evictField(identity, field, notifyCluster);
  }  

  public void clear(boolean notifyCluster) {
    if (pcache != null) pcache.clear(notifyCluster);
  }

  // -----------------------------------------------------------------------------
  // prefetch
  // -----------------------------------------------------------------------------

  // ISSUE: prefetch locally if no parent cache?

  public int prefetch(StorageAccessIF access, Object type, int field, int nextField, boolean traverse, Collection identities) {
    // WARNING: dirty objects should never be handed over to shared cache
    if (pcache != null) return pcache.prefetch(access, type, field, nextField, traverse, identities);
    return 0;
  }
  
  // -----------------------------------------------------------------------------
  // AccessRegistrarIF implementation
  // -----------------------------------------------------------------------------

  public IdentityIF createIdentity(Object type, long key) {
    return new LongIdentity(type, key);
  }

  public IdentityIF createIdentity(Object type, Object key) {
    return new AtomicIdentity(type, key);
  }
  
  public IdentityIF createIdentity(Object type, Object[] keys) {
    return new Identity(type, keys);
  }

  public TicketIF getTicket() {
    if (pregistrar == null)
      return ticket;
    else
      return pregistrar.getTicket();
  }
  
  public void registerIdentity(TicketIF ticket, IdentityIF identity) {
    if (log.isDebugEnabled())
      log.debug("Registering identity " + identity);

    // notify parent cache if no txn changes
    if (pregistrar != null && txn.isObjectClean(identity))
      pregistrar.registerIdentity(ticket, identity);

    // make sure txn holds an object instance
    txn.checkIdentityMapAndCreateInstance(identity);
  }

  public void registerField(TicketIF ticket, IdentityIF identity, int field, Object value) {
    if (log.isDebugEnabled())
      log.debug("Registering " + identity + " field " + field + "=" + value);      

    // notify parent cache if no txn changes
    if (pregistrar != null && txn.isObjectClean(identity))
      pregistrar.registerField(ticket, identity, field, value);
    
    // make sure txn holds an object instance
    txn.checkIdentityMapAndCreateInstance(identity);
  }
  
}
