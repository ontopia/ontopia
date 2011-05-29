
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
  public Object getHelperObject(int identifier, IdentityIF namespace);

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
  public Map getProperties();

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
