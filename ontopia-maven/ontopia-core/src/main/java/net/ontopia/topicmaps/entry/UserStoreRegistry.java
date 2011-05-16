
// $Id: UserStoreRegistry.java,v 1.6 2007/08/30 09:44:01 geir.gronmo Exp $

package net.ontopia.topicmaps.entry;

import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

/**
 * INTERNAL: Wrapper class for accessing a {@link StoreRegistry} using a
 * particular transaction user object.<p>
 *
 * @since 1.3.4
 * @deprecated
 */

public class UserStoreRegistry {

  protected StoreRegistry registry;
  protected Object txnuser;
  
  public UserStoreRegistry(StoreRegistry registry, Object txnuser) {
    this.registry = registry;
    this.txnuser = txnuser;
  }

  /**
   * INTERNAL: Returns the underlying store registry.
   */
  public StoreRegistry getStoreRegistry() {
    return registry;
  }

  /**
   * INTERNAL: Returns the transaction user object that is used when
   * accessing the store registry.
   */
  public Object getTransactionUser() {
    return txnuser;
  }
  
  /**
   * INTERNAL: Delegates to StoreRegistry.getStore(Object txnuser, String refkey).
   */
  public TopicMapStoreIF getStore(String refkey) {
    return registry.getStore(txnuser, refkey);
  }

  /**
   * INTERNAL: Delegates to StoreRegistry.getReferenceKey(Object txnuser, TopicMapStoreIF store).
   */
  public String getReferenceKey(TopicMapStoreIF store) {
    return registry.getReferenceKey(txnuser, store);
  }

  /**
   * INTERNAL: Delegates to StoreRegistry.getReferenceKeys(Object txnuser).
   */
  public Collection<String> getReferenceKeys() {
    return registry.getReferenceKeys(txnuser);
  }

  /**
   * INTERNAL: Delegates to StoreRegistry.getStores(Object txnuser).
   */
  public Collection<TopicMapStoreIF> getStores() {
    return registry.getStores(txnuser);
  }

  /**
   * INTERNAL: Delegates to StoreRegistry.isStoreOpen(Object txnuser, String refkey).
   */
  public boolean isStoreOpen(String refkey) {
    return registry.isStoreOpen(txnuser, refkey);
  }

  /**
   * INTERNAL: Delegates to StoreRegistry.openStore(Object txnuser,
   * String refkey, boolean readonly).
   */
  public TopicMapStoreIF openStore(String refkey, boolean readonly) throws IOException {
    return registry.openStore(txnuser, refkey, readonly);
  }

  /**
   * INTERNAL: Delegates to StoreRegistry.closeStore(Object txnuser, String refkey).
   */
  public void closeStore(String refkey) {
    registry.closeStore(txnuser, refkey);
  }
  
}
