
package net.ontopia.persistence.proxy;

import java.util.Collection;

import net.ontopia.utils.LookupIndexIF;

/**
 * INTERNAL: Cluster implementation interface.
 */

public interface ClusterIF {
  
  // event types

  public static final int QUERY_CACHE_SRCLOC_EVICT = CachesIF.QUERY_CACHE_SRCLOC;
  public static final int QUERY_CACHE_SRCLOC_CLEAR = CachesIF.QUERY_CACHE_SRCLOC + 1;

  public static final int QUERY_CACHE_SUBIND_EVICT = CachesIF.QUERY_CACHE_SUBIND;
  public static final int QUERY_CACHE_SUBIND_CLEAR = CachesIF.QUERY_CACHE_SUBIND + 1;

  public static final int QUERY_CACHE_SUBLOC_EVICT = CachesIF.QUERY_CACHE_SUBLOC;
  public static final int QUERY_CACHE_SUBLOC_CLEAR = CachesIF.QUERY_CACHE_SUBLOC + 1;

  public static final int QUERY_CACHE_RT1_EVICT = CachesIF.QUERY_CACHE_RT1;
  public static final int QUERY_CACHE_RT1_CLEAR = CachesIF.QUERY_CACHE_RT1 + 1;

  public static final int QUERY_CACHE_RT2_EVICT = CachesIF.QUERY_CACHE_RT2;
  public static final int QUERY_CACHE_RT2_CLEAR = CachesIF.QUERY_CACHE_RT2 + 1;

  public static final int DATA_CACHE_CLEAR = -1;

  public static final int DATA_CACHE_IDENTITY_EVICT = -2;
  public static final int DATA_CACHE_FIELDS_EVICT = -3;
  public static final int DATA_CACHE_FIELD_EVICT = -4;
  
  public void join();
  
  public void leave();

  public void flush();
  
  public void evictIdentity(IdentityIF identity);

  public void evictFields(IdentityIF identity);

  public void evictField(IdentityIF identity, int field);

  public void clearDatacache();
  
  public void evictCache(IdentityIF namespace, int cacheType, Object key);

  public void evictCache(IdentityIF namespace, int cacheType, Collection keys);

  public void clearCache(IdentityIF namespace, int cacheType);
  
}
