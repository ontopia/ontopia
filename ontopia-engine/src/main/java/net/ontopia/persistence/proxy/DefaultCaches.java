
package net.ontopia.persistence.proxy;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: Default caches.
 */

public class DefaultCaches implements CachesIF {
  
  DefaultCaches() {
    // NOTE: no need to hold references to the caches as only one copy
    // will every be requested anyway as we do not do any clustering.
  }
  
  public Map createDataCache() {
    return Collections.synchronizedMap(new SoftHashMap());
  }
  
  public CacheIF createCache(int cacheType, IdentityIF namespace) {
    switch (cacheType) {
    case CachesIF.QUERY_CACHE_SRCLOC:
      return createCache();
    case CachesIF.QUERY_CACHE_SUBIND:
      return createCache();
    case CachesIF.QUERY_CACHE_SUBLOC:
      return createCache();
    case CachesIF.QUERY_CACHE_RT1:
      return createCache();
    case CachesIF.QUERY_CACHE_RT2:
      return createCache();
    default:
      throw new OntopiaRuntimeException("Invalid cache type: " + cacheType);
    }
  }
  
  // --- helper

  private CacheIF createCache() {
    return new DefaultCache(new SoftHashMap());
  }
  
}
