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

import java.util.Map;
  
/**
 * INTERNAL: Interface for accessing storage definitions.<p>
 *
 * This class is similar to the JDO PersistenceManagerFactory
 * interface.<p>
 */

public interface StorageIF {

  /**
   * INTERNAL: Returns the optimized object relational mapping
   * declaration.
   */
  public RDBMSMapping getMapping();

  /**
   * INTERNAL: Returns true if shared cache is enabled.
   */
  public boolean isSharedCache();

  /**
   * INTERNAL: Returns the shared storage cache, if any.
   */
  public StorageCacheIF getStorageCache();

  /**
   * INTERNAL: Returns the shared caches.
   */
  public EvictableIF getHelperObject(int identifier, IdentityIF namespace);

  /**
   * INTERNAL: Notify cluster that transaction has been committed, so
   * that batched cluster events can be broadcasted.
   */
  public void notifyCluster();
  
  /**
   * INTERNAL: Creates a new storage access instance.
   */
  public TransactionIF createTransaction(boolean readonly);
  
  /**
   * INTERNAL: Returns the storage cache shared by all storage access
   * instances produced by this storage definition.
   */
  // WARNING: Shared cache not yet used anywhere.
  //! public StorageCacheIF getCache();

  /**
   * INTERNAL: Gets the properties held by the storage.
   */
  public Map<String, String> getProperties();

  /**
   * INTERNAL: Gets the value of the specified storage property.
   */
  public String getProperty(String property);

  /**
   * INTERNAL: Closes the storage definition, which allows it to free
   * its resources.
   */
  public void close();
  
}
