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
  
/**
 * INTERNAL: Interface used by the transaction to get hold of objects
 * and object field values. Implementations of this interface are free
 * do quite a lot of optimizations when it comes to memory handling
 * and data repository access.
 */

public interface StorageCacheIF {

  // -----------------------------------------------------------------------------
  // TransactionIF callbacks
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Can be called to verify whether the specified identity
   * exists in the cache or in the data repository. Whether the data
   * repository is actually asked depends on the policy of the storage
   * cache.
   */
  public boolean exists(StorageAccessIF access, IdentityIF identity);

  //! /**
  //!  * INTERNAL: A call to this method makes sure that the object is
  //!  * being registered with the cache. After this method has been
  //!  * called the getValue method can be used to access cache data.
  //!  */
  //! public void loadObject(IdentityIF identity);
  
  /**
   * INTERNAL: A call forwarded by the transaction (TransactionIF)
   * from persistent objects (PersistentIF) when the field value needs
   * to be retrieved from storage. The field value will be returned.
   *
   * @throws IdentityNotFoundException if the identity was not found.
   */    
  public Object getValue(StorageAccessIF access, IdentityIF identity, int field)
    throws IdentityNotFoundException;

  /**
   * INTERNAL: Can be called to check if the identity has been
   * registered with the cache. The data repository will not be asked.
   */
  public boolean isObjectLoaded(IdentityIF identity);

  /**
   * INTERNAL: Can be called to check if the specfied field has been
   * registered with the cache. The data repository will not be asked.
   */
  public boolean isFieldLoaded(IdentityIF identity, int field);

  // -----------------------------------------------------------------------------
  // eviction
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Tells the cache that eviction is starting.
   */
  public void registerEviction();
  
  /**
   * INTERNAL: Deregister eviction.
   */
  public void releaseEviction();

  /**
   * INTERNAL: Evict the identity from the cache.
   */
  public void evictIdentity(IdentityIF identity, boolean notifyCluster);

  /**
   * INTERNAL: Evict all the identity's field values from the cache.
   */
  public void evictFields(IdentityIF identity, boolean notifyCluster);

  /**
   * INTERNAL: Evict the identity's field value from the cache.
   */
  public void evictField(IdentityIF identity, int field, boolean notifyCluster);

  /**
   * INTERNAL: Clears the cache.
   */
  public void clear(boolean notifyCluster);

  // -----------------------------------------------------------------------------
  // prefetch
  // -----------------------------------------------------------------------------

  public int prefetch(StorageAccessIF access, Class<?> type, int field, int nextField, boolean traverse, Collection identities);
  
  // -----------------------------------------------------------------------------
  // AccessRegistrar
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Returns the access registrar instance that is used by
   * the storage cache. If it does not need an access registrar, or it
   * does not have one, null is returned.
   */    
  public AccessRegistrarIF getRegistrar();
  
  // Need the following methods in order to be able to invalidate and
  // update the contents of the storage cache.
  
  // public void invalidate(IdentityIF identity, int field);
  // public void invalidate(IdentityIF identity);
  // public void invalidate(IdentityIF[] identities);
  // public void invalidateAll();
  
  // public void update(IdentityIF identity, int field, Object value);
  // public void update(IdentityIF identity, Object[] field_values);

  // public StorageCacheIF getSuperCache();
  
  // -----------------------------------------------------------------------------
  // Cleanup
  // -----------------------------------------------------------------------------
  
  /**
   * INTERNAL: Releases all resources used by the storage cache.
   */
  public void close();
  
}






