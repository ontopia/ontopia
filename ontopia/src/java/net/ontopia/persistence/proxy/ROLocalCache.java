
// $Id: ROLocalCache.java,v 1.3 2007/10/03 12:09:44 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.util.Collection;

/**
 * INTERNAL: A transactional read-only storage cache
 * implementation.
 */

public class ROLocalCache extends AbstractLocalCache {

  ROLocalCache(ROTransaction txn, StorageCacheIF pcache) {
    super(txn, pcache);
  }

  // -----------------------------------------------------------------------------
  // StorageCacheIF implementation
  // -----------------------------------------------------------------------------

  public boolean exists(StorageAccessIF access, IdentityIF identity) {
    // check parent cache
    if (pcache != null)
      return pcache.exists(access, identity);
    
    // check database
    return access.loadObject(this, identity);
  }
  
  public Object getValue(StorageAccessIF access, IdentityIF identity, int field) {
    // check parent cache
    if (pcache != null) 
      return pcache.getValue(access, identity, field);
    
    // otherwise get directly from database
    return access.loadField(this, identity, field);
  }

  // -----------------------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------------------

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("proxy.ROLocalCache@");
    sb.append(System.identityHashCode(this));
    if (pcache != null)
      sb.append(" [parent = ").append(pcache).append(']');
    return sb.toString();
  }
  
}
