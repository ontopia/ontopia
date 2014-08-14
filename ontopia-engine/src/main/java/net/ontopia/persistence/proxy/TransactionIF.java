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
import net.ontopia.persistence.query.jdo.JDOQuery;

/**
 * INTERNAL: Interface that manages object transactions using the
 * proxy framework.<p>
 *
 * This class is similar to the JDO PersistenceManager, StateManager
 * and Transaction interfaces.<p>
 */

public interface TransactionIF {

  /**
   * INTERNAL: Gets the transaction id. This id is unique for a given
   * StorageIF instance.
   */
  public String getId();

  /**
   * INTERNAL: Gets the storage access used by the transaction.
   */
  public StorageAccessIF getStorageAccess();

  /**
   * INTERNAL: Gets the object access used by the transaction.
   */
  public ObjectAccessIF getObjectAccess();

  /**
   * INTERNAL: Gets the access registrar used by the transaction.
   */  
  public AccessRegistrarIF getAccessRegistrar();
  
  /**
   * INTERNAL: Returns true if this is a read-only transaction.
   */
  public boolean isReadOnly();
  
  /**
   * INTERNAL: Returns true the transaction is active.
   */
  public boolean isActive();
  
  /**
   * INTERNAL: Returns true the transaction is clean, i.e. no changes
   * have been made.
   */
  public boolean isClean();

  /**
   * INTERNAL: Returns true if the transaction is valid.
   */
  public boolean validate();
  
  /**
   * INTERNAL: Begins a new transaction.
   */
  public void begin();

  /**
   * INTERNAL: Commits the changes performed in the transaction.
   */
  public void commit();
  
  /**
   * INTERNAL: Aborts the changes performed in the transaction.
   */
  public void abort();

  /**
   * INTERNAL: Releases all resources used by the transaction.
   */
  public void close();
  
  /**
   * INTERNAL: Stores all pending changes in the data repository.
   * Note that the transaction is not commited.
   */
  public void flush();

  /**
   * INTERNAL: Gets the object instance with the given identity. If
   * the identity is known not to exist in the data repository an
   * exception will be thrown. Deleted objects will not be returned
   * from this method.
   */
  public PersistentIF getObject(IdentityIF identity);

  /**
   * INTERNAL: Gets the object instance with the given identity. If
   * the identity is known not to exist in the data repository an
   * exception will be thrown. Known and still existing object
   * instances of deleted objects will be returned from this method if
   * the acceptDeleted flag is true.
   */
  public PersistentIF getObject(IdentityIF identity, boolean acceptDeleted);

  /**
   * EXPERIMENTAL: ...
   */
  public PersistentIF _getObject(IdentityIF identity);

  public void assignIdentity(PersistentIF object);
  
  /**
   * INTERNAL: Registers the object with the transaction and marks it
   * for creation in the data repository.
   */
  public void create(PersistentIF object);
  
  /**
   * INTERNAL: Unregisters the object with the transaction and marks
   * it for deletion in the data repository.
   */
  public void delete(PersistentIF identity);

  //! /**
  //!  * EXPERIMENTAL: Refreshes the object. Evicts all fields from the
  //!  * object itself and the cache.
  //!  */
  //! public void refresh(PersistentIF object);
  //! 
  //! /**
  //!  * EXPERIMENTAL: Refreshes the object field. Evicts the field from
  //!  * the object itself and the cache.
  //!  */
  //! public void refresh(PersistentIF object, int field);
  //! //! public void refresh(PersistentIF object, int[] fields);
  
  // -----------------------------------------------------------------------------
  // TransactionIF (internal)
  // -----------------------------------------------------------------------------

  // /**
  //  * INTERNAL: Gets the access registrar instance that should retrieve
  //  * callbacks when object identities and field values are found in
  //  * the data repository.
  //  */
  // public AccessRegistrarIF getRegistrar();
  
  // /**
  //  * INTERNAL: Returns all objects created in this transaction.
  //  */
  // public Set getCreated();
  // 
  // /**
  //  * INTERNAL: Returns all objects deleted in this transaction.
  //  */
  // public Set getDeleted();
  // 
  // /**
  //  * INTERNAL: Returns all objects marked as dirty in this transaction.
  //  */
  // public Set getDirty();

  // -----------------------------------------------------------------------------
  // PersistentIF changes
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Called by PersistentIFs when the object's data has changed.
   */
  public void objectDirty(PersistentIF object);
  public void objectRead(IdentityIF identity);
  public void objectCreated(PersistentIF object);
  public void objectDeleted(PersistentIF object);

  //! /**
  //!  * INTERNAL: Called by PersistentIFs when the specified field value has
  //!  * been replaced by a new value.
  //!  */
  //! public void valueChanged(IdentityIF object, int field, Object value, boolean dchange);
  //! 
  //! /**
  //!  * INTERNAL: Called by PersistentIFs when a new value has been added
  //!  * to the specified collection field.
  //!  */
  //! public void valueAdded(IdentityIF object, int field, Object value, boolean dchange);
  //! 
  //! /**
  //!  * INTERNAL: Called by PersistentIFs when a value has been removed
  //!  * from the specified collection field.
  //!  */
  //! public void valueRemoved(IdentityIF object, int field, Object value, boolean dchange);

  // -----------------------------------------------------------------------------
  // PersistentIF management
  // -----------------------------------------------------------------------------

  //! /**
  //!  * INTERNAL: Called by PersistentIFs when the internal state of the
  //!  * specified object is to be retrieved from the data store. This
  //!  * method call may cause some fields to be retrieved from the data
  //!  * store. This behaviour may vary between object types.
  //!  */
  //! public void loadObject(PersistentIF object);

  /**
   * INTERNAL: Called by PersistentIFs when the value of the specified
   * field is requested. Note that the persistent object will be
   * notified through the _p_setValue method, so there is usually no
   * need to use the return value to set the instance member.
   *
   * @return the field value that was loaded.
   *
   * @throws IdentityNotFoundException if the identity was not found.
   */
  public <F> F loadField(IdentityIF object, int field)
    throws IdentityNotFoundException;

  /**
   * EXPERIMENTAL: 
   */
  public boolean isObjectLoaded(IdentityIF identity);
  
  /**
   * EXPERIMENTAL: 
   */
  public boolean isFieldLoaded(IdentityIF identity, int field);

  public boolean isObjectClean(IdentityIF identity);

  //! /**
  //!  * EXPERIMENTAL: 
  //!  */
  //! public boolean isObjectLoaded(IdentityIF identity);
  //! 
  //! /**
  //!  * EXPERIMENTAL: 
  //!  */
  //! public boolean isFieldLoaded(IdentityIF identity, int field);

  //! // -----------------------------------------------------------------------------
  //! // Dirty
  //! // -----------------------------------------------------------------------------
  //! 
  //! public boolean isDirty(IdentityIF identity);
  //! 
  //! public boolean isDirty(IdentityIF identity, int field);
  //! 
  //! public int nextDirty(IdentityIF identity, int start);
  //! 
  //! public int nextDirty(IdentityIF identity, int start, int end);
  //! 
  //! public void setDirty(IdentityIF identity, int field, boolean flag);

  //! // -----------------------------------------------------------------------------
  //! // Eviction
  //! // -----------------------------------------------------------------------------
  //! 
  //! /**
  //!  * EXPERIMENTAL: 
  //!  */
  //! public void evictCache(IdentityIF identity);
  //! 
  //! /**
  //!  * EXPERIMENTAL: 
  //!  */
  //! public void evictCache(IdentityIF identity, int field);
  //! //! public void evictCache(PersistentIF object, int[] fields, int offset, int length);

  // -----------------------------------------------------------------------------
  // Prefetching
  // ----------------------------------------------------------------------------

  public void prefetch(Class<?> type, int field, boolean traverse, Collection<IdentityIF> identities);

  public void prefetch(Class<?> type, int[] fields, boolean[] traverse, Collection<IdentityIF> identities);
  
  // -----------------------------------------------------------------------------
  // Queries
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Binds the specified query with the given name. The
   * query can after it has been registered later be executed by its
   * name.
   */
  // public void registerQuery(String name, QueryIF query);
  
  /**
   * INTERNAL: Executes the named query. The parameters given in the
   * params parameter are used during the execution of the query.
   */
  public Object executeQuery(String name, Object[] params);

  /**
   * INTERNAL: Executes the given query. The parameters given in the
   * params parameter are used during the execution of the query.
   */
  // public Object executeQuery(QueryIF query, Object[] params);

  /**
   * INTERNAL: Build a QueryIF from the specified JDO query instance.
   */
  public QueryIF createQuery(JDOQuery jdoquery, boolean resolve_identities);

  //! // -----------------------------------------------------------------------------
  //! // Event listeners
  //! // -----------------------------------------------------------------------------
  //! 
  //! public void addListener(TransactionEventListenerIF listener);
  //!       
  //! public void removeListener(TransactionEventListenerIF listener);
        
}
