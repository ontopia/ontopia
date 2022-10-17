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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import net.ontopia.persistence.query.jdo.JDOQuery;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The default proxy transaction implementation.
 */
public abstract class AbstractTransaction implements TransactionIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(AbstractTransaction.class.getName());

  protected boolean debug = log.isDebugEnabled();

  protected boolean isactive;
  protected boolean isclosed;
  protected boolean isaborted = false;

  protected String id;
  protected StorageAccessIF access;

  protected StorageCacheIF txncache;
  protected AccessRegistrarIF registrar;
  protected ObjectAccessIF oaccess;
  
  protected ObjectRelationalMappingIF mapping;
  
  protected final Map<IdentityIF, PersistentIF> identity_map;
  protected Map<IdentityIF, PersistentIF> lru;
  protected int lrusize;

  protected Map<String, QueryIF> querymap;
  protected long timestamp;
  
  AbstractTransaction(String id, StorageAccessIF access) {
    this.id = id;
    this.access = access;
    this.mapping = access.getStorage().getMapping();

    // Map containing named queries
    this.querymap = new HashMap<String, QueryIF>();
    
    // Identity map - maintains the relationships between object
    // identities and the single(ton) instances used with the
    // transaction. This enforces the constraint that only one
    // instance per object identity should exist at a given time in a
    // transaction.
    
    // NOTE: Even though it's the keys that are garbage collected
    // there is a strict mapping between IdentityIF and PersistentIF
    // that lets us do this, i.e. the objects reference their
    // identities, so the identity will not be garbage collected as
    // long as the object is reachable.
    this.identity_map = new ReferenceMap<IdentityIF, PersistentIF>(AbstractReferenceMap.ReferenceStrength.HARD, AbstractReferenceMap.ReferenceStrength.SOFT);

    log.debug(getId() + ": Transaction created.");
    this.timestamp = System.currentTimeMillis();
  }

  // -----------------------------------------------------------------------------
  // TransactionIF (public)
  // -----------------------------------------------------------------------------

  @Override
  public String getId() {
    return id;
  }
  
  @Override
  public StorageAccessIF getStorageAccess() {
    return access;
  }
  
  @Override
  public boolean isActive() {
    return isactive;
  }

  @Override
  public boolean validate() {    
    if (isclosed)
      return false;
    else
      return access.validate();
  }

  @Override
  public synchronized void begin() {
    if (isclosed) throw new OntopiaRuntimeException("Cannot restart a closed transaction.");
    this.isactive = true;
    // Notify transaction cache
    log.debug(getId() + ": Transaction started.");
  }
  
  @Override
  public synchronized void commit() {
    if (!isactive) throw new OntopiaRuntimeException("Transaction is not active.");
    
    // Store transaction changes
    flush();

    // Before txn commit
    transactionPreCommit();

    // Commit storage transaction
    access.commit();

    // After txn commit
    transactionPostCommit();

    log.debug(getId() + ": Transaction committed.");
  }
  
  @Override
  public synchronized void abort() {
    if (!isactive) throw new OntopiaRuntimeException("Transaction is not active.");

    // Rollback all changes
    transactionPreAbort();
    try {
      access.abort();    
    } catch (Throwable t) {
      // ignore, because the txn will be invalid anyway
    }
    isaborted = true;
    transactionPostAbort();

    log.debug(getId() + ": Transaction aborted.");
  }

  @Override
  public synchronized void close() {
    if (isclosed) throw new OntopiaRuntimeException("Transaction is already closed.");
    // Note: access is closed here.
    access.close();
    
    log.debug(getId() + ": Transaction closed.");
    this.isclosed = true;
    this.isactive = false;

    ((RDBMSStorage)access.getStorage()).transactionClosed(this);
  }

  @Override
  public abstract void flush();

  protected abstract void transactionPreCommit();
  protected abstract void transactionPostCommit();

  protected abstract void transactionPreAbort();
  protected abstract void transactionPostAbort();

  @Override
  public ObjectAccessIF getObjectAccess() {
    return oaccess;
  }

  @Override
  public AccessRegistrarIF getAccessRegistrar() {
    return registrar;
  }

  // -----------------------------------------------------------------------------
  // Misc. PersistentIF callbacks
  // -----------------------------------------------------------------------------
  
  @Override
  public boolean isObjectLoaded(IdentityIF identity) {
    if (!isactive && isaborted) { throw new TransactionNotActiveException(); }
    
    // check identity map
    synchronized (identity_map) { // read
      if (checkIdentityMapNoLRU(identity) != null) return true;
    }
    
    // check to see if object is registered in cache
    return txncache.isObjectLoaded(identity);
  }
  
  @Override
  public boolean isFieldLoaded(IdentityIF identity, int field) {
    if (!isactive && isaborted) { throw new TransactionNotActiveException(); }
    
    // check identity map
    synchronized (identity_map) { // read
      PersistentIF p = checkIdentityMapNoLRU(identity);
      if (p == null) return false;
      if (p.isLoaded(field)) return true;
    }
    
    // Check to see if field is registered in cache
    return txncache.isFieldLoaded(identity, field);
  }
  
  @Override
  public <F> F loadField(IdentityIF identity, int field) {
    if (!isactive && isaborted) { throw new TransactionNotActiveException(); }
    
    // NOTE: this methods is always called by a PersistentIF
    // NOTE: no need to check identity map first
    
    // get value from shared cache
    Object value = txncache.getValue(access, identity, field);
    
    // track all changes
    objectRead(identity);
    
    // look up identity value    
    if (value != null) {
      if (value instanceof IdentityIF)
        return (F) getObject((IdentityIF)value);
    }
    return (F) value;
  }

  /**
   * INTERNAL: Called by other transactions to notify this transaction of
   * committed merges. Default implementation is empty.
   * @param source The identity of the object merged into target
   * @param target The identity of the target object that was merged
   * @since %NEXT%
   */
  public synchronized void objectMerged(IdentityIF source, IdentityIF target) {
    // does noting by default, see RWTransaction
  }
  
  // -----------------------------------------------------------------------------
  // Object lookup
  // -----------------------------------------------------------------------------
  
  @Override
  public PersistentIF getObject(IdentityIF identity) {
    return getObject(identity, false);
  }
  
  @Override
  public PersistentIF getObject(IdentityIF identity, boolean acceptDeleted) {
    PersistentIF o = _getObject(identity);
    if (o != null && o.isDeleted())
      return (acceptDeleted ? o : null);
    else
      return o;
  }
  
  @Override
  public PersistentIF _getObject(IdentityIF identity) {
    if (!isactive && isaborted) { throw new TransactionNotActiveException(); }
    
    Objects.requireNonNull(identity, "null identities should not be looked up.");
    
    // Check local identity map
    synchronized (identity_map) { // read
      // check identity map
      PersistentIF p = checkIdentityMap(identity);
      if (p != null && !p.isTransient()) {
        return p;
      }
    }
    
    //! // Object was not found in the identity map, so we need to store
    //! // transaction changes, to make sure that deleted objects are
    //! // deleted in the database.
    //! flush();
    
    // FIXME: Is it faster to loop over deleted objects in the change set?
    
    // The instance is not in the identity map, so we need to
    // prepare a new instance.
    
    // Ask transaction cache to perform existence check. If the call
    // succeeded we know that the object exists in the data
    // repository. The identity will also be registered with the
    // appropriate access registrar.
    if (!txncache.exists(access, identity))
      throw new IdentityNotFoundException(identity);
    
    if (log.isDebugEnabled())
      log.debug(getId() + ": Identity found in data repository: " + identity);
    
    return checkIdentityMapAndCreateInstance(identity);
  }
  
  // -----------------------------------------------------------------------------
  // Identity map management methods
  // -----------------------------------------------------------------------------
  
  protected PersistentIF checkIdentityMapAndCreateInstance(IdentityIF identity) {
    // NOTE: now rechecking identity map because registrar might have
    // been here and created an instance for us. At this point we know
    // that the identity exists.
    
    // prevent somebody else tampering with the identity map
    synchronized (identity_map) { // read, then write
      // check identity map
      PersistentIF p = checkIdentityMapNoLRU(identity);
      if (p != null) {
        // set state if transient
        if (p.isTransient()) {
          p.setPersistent(true);
          p.setInDatabase(true);
        }
        return p;
      }
      
      // create new instance
      p = createInstance(identity);
      // set state
      if (!isReadOnly()) {
        p.setPersistent(true);
        p.setInDatabase(true);
      }
      return p;
    }
  }
  
  protected PersistentIF checkIdentityMapNoLRU(IdentityIF identity) {
    // WARNING: access to this method should be synchronized on identity_map
    
    // Check to see if somebody else has registered the same identity
    return identity_map.get(identity);
  }
  
  protected PersistentIF removeIdentityMapNoLRU(IdentityIF identity) {
    // WARNING: access to this method should be synchronized on identity_map
    // ISSUE: remove from lru as well?
    
    // Check to see if somebody else has registered the same identity
    return identity_map.remove(identity);
  }
  
  protected PersistentIF checkIdentityMap(IdentityIF identity) {
    // WARNING: access to this method should be synchronized on identity_map
    
    // Check to see if somebody else has registered the same identity
    PersistentIF o = identity_map.get(identity);
    if (o != null) {
      //! if (log.isDebugEnabled())
      //!   log.debug(getId() + ": Object found in identity map: " + identity);
      
      // Register with LRU cache
      lru.put(identity, o);
      // Return singleton object instance 
      return o;
    }
    return null;
  }
  
  protected PersistentIF createInstance(IdentityIF identity) {
    try {
      
      // Create instance of identity type class
      //! PersistentIF object = (PersistentIF)identity.createInstance();
      ClassInfoIF cinfo = mapping.getClassInfo(identity.getType());
      PersistentIF object = (PersistentIF)cinfo.createInstance(isReadOnly());
      
      // Register identity with persistent object
      object._p_setIdentity(identity);
      
      // Register transaction state
      object._p_setTransaction(this);
      
      // Note: The entry value must also be a soft reference, since the
      // object references the key (its identity)! Thus an LRU cache
      // might have to be used somewhere to avoid references being
      // evicted too often.
      
      // Register object with identity map
      identity_map.put(identity, object);
      // Register with LRU cache
      lru.put(identity, object);
      
      // TODO: Should register with AccessRegistrarIF here?        
      // NOTE: No need to set loaded members because they will be
      // retrieved lazily when needed.
      
      // Return newly created object
      return object;
      
    } catch (RuntimeException e1) {
      throw e1;
    } catch (Exception e2) {
      throw new OntopiaRuntimeException(e2);
    }
  }

  // -----------------------------------------------------------------------------
  // Prefetching
  // -----------------------------------------------------------------------------
  
  @Override
  public void prefetch(Class<?> type, int field, boolean traverse, Collection<IdentityIF> identities) {
    // bug #1439: do not prefetch if identity is altered by local transaction
    identities = extractNonDirty(identities);
    
    // prefetch field values
    if (log.isDebugEnabled())
      log.debug("Prefetching field: " + field + " " + type + " " + identities.size());
    this.txncache.prefetch(access, type, field, -1, traverse, identities);
  }
  
  @Override
  public void prefetch(Class<?> type, int[] fields, boolean[] traverse, Collection<IdentityIF> identities) {
    // bug #1439: do not prefetch if identity is altered by local transaction
    identities = extractNonDirty(identities);
    
    if (log.isDebugEnabled())
      log.debug("Prefetching fields: " + StringUtils.join(fields, ",") + " " + type + " " + identities.size());
    ClassInfoIF cinfo = mapping.getClassInfo(type);
    for (int i=0; i < fields.length; i++) {
      // prefetch field values
      boolean moreFields = (i+1 < fields.length);
      //! System.out.println("PFx: " + fields[i] +  " " + type + " " + identities.size());
      int prefetched =  this.txncache.prefetch(access, type, fields[i], 
          (moreFields ? fields[i+1] : -1), 
          traverse[i], identities);
      if (prefetched == 0) return;
      // get next type
      if (moreFields) {
        // extract prefetched field values
        identities = extractFieldValues(type, fields[i], identities);
        // update type information
        cinfo = cinfo.getValueFieldInfos()[fields[i]].getValueClassInfo();
        type = cinfo.getDescriptorClass();
      }
    }
  }
  
  protected Collection<IdentityIF> extractNonDirty(Collection<IdentityIF> identities) {
    // get rid of identities that are dirty in this transaction
    Collection<IdentityIF> result = new HashSet<IdentityIF>(identities.size());
    for (IdentityIF identity : identities) {
      
      // bug #1439: do not prefetch if identity is altered by local transaction
      if (!isObjectClean(identity)) continue;
      
      result.add(identity);
    }
    return result;
  }
  
  protected Collection extractFieldValues(Object type, int field, Collection<IdentityIF> identities) {    
    Collection result = new HashSet(identities.size());
    for (IdentityIF identity : identities) {
      
      // bug #1439: do not prefetch if identity is altered by local transaction
      if (!isObjectClean(identity)) continue;
      
      // get field value from cache
      Object value = txncache.getValue(access, identity, field);
      if (value == null) continue;
      if (value instanceof Collection) {
        Collection coll = (Collection)value;
        if (!coll.isEmpty()) result.addAll(coll);
      } else {
        result.add(value);   
      }
    }
    return result;
  }
  
  // -----------------------------------------------------------------------------
  // Queries
  // -----------------------------------------------------------------------------
  
  @Override
  public Object executeQuery(String name, Object[] params) {
    if (!isactive) throw new TransactionNotActiveException();
    
    try {
      // Look up query
      QueryIF query = getQuery(name);      
      // store changes up to this point (conforming queries)
      flush();
      // Execute query
      return query.executeQuery(params);
      
    } catch (RuntimeException e1) {
      throw e1;
    } catch (Exception e2) {
      throw new OntopiaRuntimeException(e2);
    }
  }
  
  @Override
  public QueryIF createQuery(JDOQuery jdoquery, boolean resolve_identities) {
    if (!isactive) throw new TransactionNotActiveException();
    
    // FIXME: Move this method elsewhere?
    return access.createQuery(jdoquery, oaccess, registrar, resolve_identities);
  }
  
  protected QueryIF getQuery(String name) {
    QueryIF query = querymap.get(name);
    if (query == null) {
      // Create and register query instance lazily
      query = access.createQuery(name, oaccess, registrar);
      registerQuery(name, query);
    }
    return query;
  }
  
  protected void registerQuery(String name, QueryIF query) {
    if (log.isDebugEnabled())
      log.debug(getId() + ": Registering query '" + name + "'");
    querymap.put(name, query);
  }
  
  public void writeIdentityMap(java.io.Writer out, boolean dump) throws java.io.IOException {
    out.write("<p>Cache size: " + identity_map.size() + ", LRU size: " + lru.size() + " / " + lrusize + "<br>\n");
    out.write("Created: " + new Date(timestamp) + " (" + (System.currentTimeMillis()-timestamp) + " ms)</p>\n");
    
    if (dump) {
      out.write("<table>\n");
      for (Map.Entry<IdentityIF, PersistentIF> entry : identity_map.entrySet()) {
        IdentityIF key = entry.getKey();
        PersistentIF val = entry.getValue();
        out.write("<tr><td>");
        out.write((key == null ? "null" : net.ontopia.utils.StringUtils.escapeHTMLEntities(key.toString())));
        out.write("</td><td>");
        out.write((val == null ? "null" : net.ontopia.utils.StringUtils.escapeHTMLEntities(val.toString())));
        out.write("</td></tr>\n");
      }
      out.write("</table><br>\n");
    }
  }
  
  // -----------------------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------------------
  
  @Override
  public String toString() {
    return "<Transaction " + getId() + ">";
  }
  
}
