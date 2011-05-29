
package net.ontopia.persistence.proxy;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */

public interface CachesIF {
  
  public static final int QUERY_CACHE_SRCLOC = 2;
  public static final int QUERY_CACHE_SUBIND = 4;
  public static final int QUERY_CACHE_SUBLOC = 8;
  public static final int QUERY_CACHE_RT1 = 16;
  public static final int QUERY_CACHE_RT2 = 32;

  public Map createDataCache();

  public CacheIF createCache(int cacheType, IdentityIF namespace);
  
}
