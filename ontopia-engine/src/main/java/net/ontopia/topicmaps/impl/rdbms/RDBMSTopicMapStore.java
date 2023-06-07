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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.AbstractTransaction;
import net.ontopia.persistence.proxy.ConnectionFactoryIF;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.PersistentIF;
import net.ontopia.persistence.proxy.RDBMSAccess;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.persistence.proxy.RWTransaction;
import net.ontopia.persistence.proxy.StorageCacheIF;
import net.ontopia.persistence.proxy.StorageIF;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.NotRemovableException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.StoreNotOpenException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapTransaction;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: The rdbms topic map store implementation.<p>
 */
public class RDBMSTopicMapStore extends AbstractTopicMapStore {

  protected static final Class[] types = new Class[] { Association.class, AssociationRole.class, TopicName.class, Occurrence.class, Topic.class, TopicMap.class, VariantName.class };

  protected long topicmap_id;
  protected RDBMSStorage storage;
  protected String propfile;
  protected Map<String, String> properties;
  protected RDBMSTopicMapTransaction transaction;

  protected boolean storage_local = false;
  
  /**
   * PUBLIC: Creates a new topic map store without a specified
   * database property file. A new topic map is created in the
   * repository with this constructor at the time the topic map is
   * accessed.
   */
  public RDBMSTopicMapStore() throws IOException {
    this(-1);
  }
  
  /**
   * PUBLIC: Creates a new topic map store without a specified
   * database property file. The store references an existing topic
   * map with the specified id.
   */
  public RDBMSTopicMapStore(long topicmap_id) throws IOException {
    this.storage = new RDBMSStorage();
    this.storage_local = true;
    this.topicmap_id = topicmap_id;
  }

  /**
   * PUBLIC: Creates a new topic map store with the database property
   * file set. A new topic map is created in the repository with this
   * constructor at the time the topic map is accessed.
   * @param propfile Path reference to a Java properties file.
   */
  public RDBMSTopicMapStore(String propfile) throws IOException {
    this(propfile, -1);
  }
  
  /**
   * PUBLIC: Creates a new topic map store with the database property
   * file set. The store references an existing topic map with the
   * specified id.
   * @param propfile Path reference to a Java properties file.
   * @param topicmap_id The ID of the topic map in the database.
   */
  public RDBMSTopicMapStore(String propfile, long topicmap_id) throws IOException {
    this.propfile = propfile;
    this.storage = new RDBMSStorage(propfile);
    this.storage_local = true;
    this.topicmap_id = topicmap_id;
  }

  /**
   * PUBLIC: Creates a new topic map store with the specified database
   * properties. A new topic map is created in the repository with
   * this constructor at the time the topic map is accessed.
   *
   * @since 1.2.4
   */
  public RDBMSTopicMapStore(Map<String, String> properties) throws IOException {
    this(properties, -1);
  }
  
  /**
   * PUBLIC: Creates a new topic map store with the specified database
   * properties. The store references an existing topic map with the
   * specified id.
   *
   * @since 1.2.4
   */
  public RDBMSTopicMapStore(Map<String, String> properties, long topicmap_id) throws IOException {
    this.properties = properties;
    this.storage = new RDBMSStorage(properties);
    this.storage_local = true;
    this.topicmap_id = topicmap_id;
  }
  
  /**
   * INTERNAL:
   */
  public RDBMSTopicMapStore(StorageIF storage) {
    this(storage, -1);
  }
  
  /**
   * INTERNAL:
   */
  public RDBMSTopicMapStore(StorageIF storage, long topicmap_id) {
    this.storage = (RDBMSStorage)storage;
    this.topicmap_id = topicmap_id;
  }
 
  /**
   * INTERNAL: Returns the proxy storage implementation used by the
   * topic map store.
   */
  public RDBMSStorage getStorage() {
    if (storage_local && storage == null) {
      try {
        if (propfile != null) {
          this.storage = new RDBMSStorage(propfile);
        } else if (properties != null) {
          this.storage = new RDBMSStorage(properties);
        } else {
          this.storage = new RDBMSStorage();
        }
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    return storage;
  }

  @Override
  public int getImplementation() {
    return TopicMapStoreIF.RDBMS_IMPLEMENTATION;
  }
  
  @Override
  public boolean isTransactional() {
    return true;
  }

  @Override
  public LocatorIF getBaseAddress() {
    if (base_address != null) { 
      return base_address;
    } else if (readonly) {
      return ((ReadOnlyTopicMap)getTopicMap()).getBaseAddress();
    } else {
      return ((TopicMap)getTopicMap()).getBaseAddress();
    }
  }

  @Override
  public void setBaseAddress(LocatorIF base_address) {
    this.base_address = null;
    // update persistent field
    if (readonly) {
      ((ReadOnlyTopicMap)getTopicMap()).setBaseAddress(base_address);
    } else {
      ((TopicMap)getTopicMap()).setBaseAddress(base_address);
    }
  }

  /**
   * INTERNAL: Sets the apparent base address of the store. The value
   * of this field is not considered persistent and may for that
   * reason be transaction specific.
   */
  public void setBaseAddressOverride(LocatorIF base_address) {
    this.base_address = base_address;
  }

  public TransactionIF getTransactionIF() {
    RDBMSTopicMapTransaction txn = (RDBMSTopicMapTransaction)getTransaction();
    return txn.getTransaction();
  }

  @Override
  public TopicMapTransactionIF getTransaction() {
    // Open store automagically if store is not open at this point.
    if (!isOpen()) {
      open();
    }
    
    // Create a new transaction if it doesn't exist or it has been
    // deactivated.
    if (transaction == null || !transaction.isActive()) {
      transaction = new RDBMSTopicMapTransaction(this, topicmap_id, readonly);
    }
    return transaction;
  }

  @Override
  public TopicMapIF getTopicMap() {
    return getTransaction().getTopicMap();
  }
  
  @Override
  public void commit() {
    if (transaction != null) {
      transaction.commit();
      this.topicmap_id = transaction.getActualId();
    }
  }

  @Override
  public void abort() {
    if (transaction != null) {
      transaction.abort();
    }
  }
  
  public void clear() {
    if (readonly) {
      throw new ReadOnlyException();
    }

    // Do direct table row deletion here since it is much more efficient.
    try {
      Utils.clearTopicMap(getTopicMap());

    } catch (java.sql.SQLException e) {
      throw new OntopiaRuntimeException("Could not clear topicmap. Transaction has been rolled back.", e);
    }
  }
  
  protected boolean delete(RDBMSTopicMapReference ref) {
    delete(true);
    return deleted;
  }
  
  @Override
  public void delete(boolean force) throws NotRemovableException {
    if (readonly) {
      throw new ReadOnlyException();
    }
    if (deleted) {
      return;
    }

    // check that parent source allows deleting
    if (reference != null) {
      TopicMapSourceIF source = reference.getSource();
      if (source != null && !source.supportsDelete()) {
        throw new NotRemovableException("Cannot delete topic map as the parent topic map source does not allow deleting.");
      }        
    }
    
    // Get topic map
    TopicMapIF tm = getTopicMap();

    if (!force) {
      // If we're not forcing, complain if the topic map contains any data.
      if (!tm.getTopics().isEmpty()) {
        throw new NotRemovableException("Cannot delete topic map when it contains topics.");
      }
      if (!tm.getAssociations().isEmpty()) {
        throw new NotRemovableException("Cannot delete topic map when it contains associations.");
      }
    }

    flush();
    
    // Do direct table row deletion here since it is much more efficient.
    try {
      Utils.deleteTopicMap(tm);
      
      // Commit and close transaction
      commit();
      deleted = true;
    } catch (java.sql.SQLException e) {
      throw new OntopiaRuntimeException("Could not delete topicmap. Transaction has been rolled back.", e);
    } finally {
      close();
    }
    // TODO: drop store from pool and delete from repository
  }

  /**
   * INTERNAL: Gets the value of the specified store property.
   */
  @Override
  public String getProperty(String name) {
    return getStorage().getProperty(name);
  }

  @Override
  protected void finalize() {
    // Close store when garbage collected.
    if (isOpen()) {
      close();
    }
  }

  /* -- store pool -- */
  
  @Override
  public void close() {
    // return to reference or close
    close((reference != null));
  }

  @Override
  public void close(boolean returnStore) {
    if (closed) { return; }

    if (returnStore) {

      // return store
      if (reference != null) {
        
        // rollback, but not invalidate, open transaction
        if (transaction != null) {
          ((AbstractTopicMapTransaction)transaction).abort(false);
        }
        
        // notify topic map reference that store has been closed.
        reference.storeClosed(this);
      } else {
        throw new OntopiaRuntimeException("Cannot return store when not attached to topic map reference.");
      }
    } else {
      // physically close store
      if (!isOpen()) {
        throw new StoreNotOpenException("Store is not open.");
      }
      
      // reset reference
      reference = null;

      // close transaction
      if (transaction != null) {
        transaction.abort(true);
      }
      
      // if storage is not shared close it as well
      if (storage_local && storage != null) {
        storage.close();
        storage = null;
      }
      
      // set open flag to false and closed to true
      open = false;
      closed = true;
    }
  }

  @Override
  public boolean validate() {    
    // if we're closed then not valid
    if (closed) {
      return false;
    }
    // delegate to transaction
    if (transaction != null) {
      return ((AbstractTopicMapTransaction)transaction).validate();
    } else {
      return true;
    }
  }

  // -- cache statistics

  /**
   * INTERNAL: Evicts the given object from the shared RDBMS caches.
   *
   * @since 3.3.0
   */
  public void evictObject(String object_id) {
    if (transaction != null) {
      if (storage != null) {
        StorageCacheIF scache = storage.getStorageCache();
        if (scache != null) {
          IdentityIF identity = getIdentityForObjectId(transaction.getTransaction(), object_id);
          scache.evictIdentity(identity, true);
        }
      }
    }
  }

  /**
   * INTERNAL: Empties the shared RDBMS caches.
   *
   * @since 2.2.1
   */
  public void clearCache() {
    if (transaction != null) {
      //! RDBMSStorage storage = (RDBMSStorage)transaction.getTransaction().getStorageAccess().getStorage();
      // clear shared caches
      if (storage != null) {
        storage.clearCache();
      }
    }
  }
  
  /**
   * EXPERIMENTAL: Writes a cache statistics report to the given
   * file.
   * @param filename the name of the file to write the report to
   * @param dumpCaches whether to include detailed cache dumps
   *
   * @since 2.2.1
   */
  public void writeReport(String filename, boolean dumpCaches) throws IOException {
    if (transaction != null && transaction.isActive()) {
      java.io.FileWriter out = new java.io.FileWriter(filename);
      try {
        writeReport(out, dumpCaches);
      } finally {
        out.close();
      }
    }
  }

  /**
   * EXPERIMENTAL: Writes a cache statistics report to the given writer.
   * @param out the writer to write the report to
   * @param dumpCaches whether to include detailed cache dumps
   *
   * @since 2.2.1
   */
  public void writeReport(java.io.Writer out, boolean dumpCaches) throws IOException {
    if (transaction != null && transaction.isActive()) {
      //! RDBMSStorage storage = (RDBMSStorage)transaction.getTransaction().getStorageAccess().getStorage();
      if (storage != null) {
        IdentityIF namespace = ((PersistentIF)getTopicMap())._p_getIdentity();
        storage.writeReport(out, reference, namespace, dumpCaches);
      }
    }
  }

  /**
   * EXPERIMENTAL: Dumps the identity map to the given writer.
   * @param out the writer to write the report to
   *
   * @since 3.0
   */
  public void writeIdentityMap(java.io.Writer out, boolean dump)
    throws IOException {
    if (transaction != null && transaction.isActive()) {
      AbstractTransaction txn = (AbstractTransaction)transaction.getTransaction();
      txn.writeIdentityMap(out, dump);
    }
  }
    
  // ---------------------------------------------------------------------------
  // Prefetch objects by object id
  // ---------------------------------------------------------------------------

  public boolean prefetchObjectsById(Collection<String> object_ids) {
    TransactionIF txn = transaction.getTransaction();
    // create a map per object type
    Map<Class<?>, Collection<IdentityIF>> idmap = new HashMap<Class<?>, Collection<IdentityIF>>();
    for (String object_id : object_ids) {
      IdentityIF identity = getIdentityForObjectId(txn, object_id);
      if (identity == null) {
        continue;
      }
      if (txn.isObjectLoaded(identity)) {
        continue;
      }

      Collection<IdentityIF> ids = idmap.get(identity.getType());
      if (ids == null) {
        ids = new ArrayList<IdentityIF>();
        idmap.put(identity.getType(), ids);
      }
      ids.add(identity);
    }
    Iterator<Class<?>> keys = idmap.keySet().iterator();
    while (keys.hasNext()) {
      // prefetch TMObjectIF_topicmap (always field number 1)
      Class<?> type = keys.next();
      Collection<IdentityIF> identities = idmap.get(type);
      txn.prefetch(type, 1, false, identities);
    }
    return true;
  }

  public boolean prefetchFieldsById(Collection<String> object_ids, int field) {
    TransactionIF txn = transaction.getTransaction();
    // create a map per object type
    Map<Class<?>, Collection<IdentityIF>> idmap = new HashMap<Class<?>, Collection<IdentityIF>>();
    for (String object_id : object_ids) {
      IdentityIF identity = getIdentityForObjectId(txn, object_id);
      if (identity == null) {
        continue;
      }
      Collection<IdentityIF> ids = idmap.get(identity.getType());
      if (ids == null) {
        ids = new ArrayList<IdentityIF>();
        idmap.put(identity.getType(), ids);
      }
      ids.add(identity);
    }
    Iterator<Class<?>> keys = idmap.keySet().iterator();
    while (keys.hasNext()) {
      // prefetch TMObjectIF_topicmap (always field number 1)
      Class<?> type = keys.next();
      Collection<IdentityIF> identities = idmap.get(type);
      txn.prefetch(type, field, false, identities);
    }
    return true;
  }

  protected IdentityIF getIdentityForObjectId(TransactionIF txn, String object_id) {
    long numid;
    try {
      numid = Long.parseLong(object_id.substring(1), 10);
    } catch (NumberFormatException e) {
      return null; // if not a valid ID no object will have it... :)
    }
    switch ( object_id.charAt(0) ) {
    case 'T':
      return txn.getAccessRegistrar().createIdentity(Topic.class, numid);
    case 'A':
      return txn.getAccessRegistrar().createIdentity(Association.class, numid);
    case 'O':
      return txn.getAccessRegistrar().createIdentity(Occurrence.class, numid);
    case 'B':
      return txn.getAccessRegistrar().createIdentity(TopicName.class, numid);
    case 'N':
      return txn.getAccessRegistrar().createIdentity(VariantName.class, numid);
    case 'R':
      return txn.getAccessRegistrar().createIdentity(AssociationRole.class, numid);
    //! case 'M':
    //!   return txn.getAccessRegistrar().createIdentity(TopicMap.class, numid);
    default:
      return null;
    }
  }

  // ---------------------------------------------------------------------------
  // Prefetching
  // ---------------------------------------------------------------------------

  public boolean prefetch(int type, int field, boolean traverse, Collection objects) {
    TransactionIF txn = transaction.getTransaction();

    int size = objects.size();
    if (size == 0) {
      return false;
    }
    //! if (size <= 3) return false;

    Collection identities = new ArrayList(size);
    Iterator iter = objects.iterator();
    for (int i=0; i < size; i++) {
      PersistentIF o = (PersistentIF)iter.next();
      if (o != null) {
        // filter out objects by getClass() == types[type]
        IdentityIF identity = o._p_getIdentity();
        if (types[type].equals(identity.getType())) {
          identities.add(identity);
        }
        //! else
        //!   new RuntimeException("X: " + o + " not of expected type " + types[type]).printStackTrace();
      }
    }
    txn.prefetch(types[type], field, traverse, identities);

    return true;
  }

  public boolean prefetch(int type, int[] fields, boolean[] traverse, Collection objects) {
    TransactionIF txn = transaction.getTransaction();

    int size = objects.size();
    if (size == 0) {
      return false;
    }
    //! if (size <= 3 && fields.length < 2) return false;

    Collection identities = new ArrayList(size);
    Iterator iter = objects.iterator();
    for (int i=0; i < size; i++) {      
      // TODO: filter out objects by getClass() == types[type]
      PersistentIF o = (PersistentIF)iter.next();
      if (o != null) {
        IdentityIF identity = o._p_getIdentity();
        if (types[type].equals(identity.getType())) {
          identities.add(identity);
        }
        //! else
        //!   new RuntimeException("Y: " + o + " not of expected type " + types[type]).printStackTrace();
      }
    }
    txn.prefetch(types[type], fields, traverse, identities);

    return true;
  }
    
  // ---------------------------------------------------------------------------
  // Prefetch: roles by type and association type
  // ---------------------------------------------------------------------------

  public void prefetchRolesByType(Collection players, 
                                  TopicIF rtype, TopicIF atype) {
    transaction.prefetchRolesByType(players, rtype, atype);
  }
  
  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  public long getLongId() {
    return transaction.getActualId();
  }

  public long getLongId(TMObjectIF o) {
    IdentityIF identity = ((PersistentIF)o)._p_getIdentity();
    return ((Long)identity.getKey(0)).longValue();
  }

  public void flush() {
    TransactionIF txn = transaction.getTransaction();
    txn.flush();
  }

  public java.sql.Connection getConnection() {
    return ((RDBMSAccess)transaction.getTransaction().getStorageAccess()).getConnection();
  }

  public ConnectionFactoryIF getConnectionFactory(boolean readonly) {
    return getStorage().getConnectionFactory(readonly);
  }

  public String getQueryString(String name) {
    return getStorage().getQueryString(name);
  }

  /**
   * INTERNAL: Called by MergeUtils to notify transaction of a performed merge.
   * @param source
   * @param target
   */
  public void merged(TMObjectIF source, TMObjectIF target) {
    TransactionIF tnx = getTransactionIF();
    if (tnx instanceof RWTransaction) {
      ((RWTransaction)tnx).registerMerge((TMObject) source, (TMObject) target);
    }
  }

  // ---------------------------------------------------------------------------
  // EventManagerIF: for testing purposes only
  // ---------------------------------------------------------------------------

  @Override
  public EventManagerIF getEventManager() {
    return (EventManagerIF)transaction;
  }
  
}
