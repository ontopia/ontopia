
package net.ontopia.persistence.proxy;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: Caches used by JGroups cluster.
 */

public class JGroupsCaches implements CachesIF {

  final ClusterIF cluster;
  
  JGroupsCaches(ClusterIF cluster) {
    this.cluster = cluster;
  }
  
  public Map createDataCache() {
    return Collections.synchronizedMap(new SoftHashMap());
  }
  
  public CacheIF createCache(int cacheType, IdentityIF namespace) {
    switch (cacheType) {
    case CachesIF.QUERY_CACHE_SRCLOC:
      return _createCache(cacheType, namespace);
    case CachesIF.QUERY_CACHE_SUBIND:
      return _createCache(cacheType, namespace);
    case CachesIF.QUERY_CACHE_SUBLOC:
      return _createCache(cacheType, namespace);
    case CachesIF.QUERY_CACHE_RT1:
      return _createCache(cacheType, namespace);
    case CachesIF.QUERY_CACHE_RT2:
      return _createCache(cacheType, namespace);
    default:
      throw new OntopiaRuntimeException("Invalid cache type: " + cacheType);
    }
  }

  // --- helper
    
  private CacheIF _createCache(int cacheType, IdentityIF namespace) {
    return new ClusteredCache(new SoftHashMap(), this.cluster, cacheType, namespace);
  }
  
}
