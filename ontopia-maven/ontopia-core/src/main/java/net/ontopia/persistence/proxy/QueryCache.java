
// $Id: QueryCache.java,v 1.25 2007/10/03 11:33:33 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.persistence.query.sql.DetachedQueryIF;
import net.ontopia.utils.LookupIndexIF;
import net.ontopia.utils.NullObject;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.SynchronizedLookupIndex;

import org.apache.commons.collections.map.LRUMap;

/**
 * INTERNAL: A storage access implementation accessing relational
 * databases using JDBC.
 */

public class QueryCache implements EvictableIF {

  protected DetachedQueryIF query;
  protected CacheIF cache;
  protected Map lru;
  protected int lrusize;
  
  QueryCache(DetachedQueryIF query, CacheIF cache, int lrusize) {
    this.query = query;
    this.cache = cache;
    this.lru = Collections.synchronizedMap(new LRUMap(lrusize));
    this.lrusize = lrusize;
  }
  
  public Object executeQuery(StorageAccessIF access, Object cachekey, Object[] query_params) {
    try {
      Object result = cache.get(cachekey);
      if (result == null) {
        // cache miss
        result = query.executeQuery(((RDBMSAccess)access).getConnection(), query_params);
        cache.put(cachekey, (result == null ? NullObject.INSTANCE : result));
        lru.put(cachekey, (result == null ? NullObject.INSTANCE : result));
        return result;
      } else {
        // cache hit
        lru.put(cachekey, result);
        return (ObjectUtils.equals(NullObject.INSTANCE, result) ? null : result);
      }
    } catch (RuntimeException e1) {
      throw e1;
    } catch (Exception e2) {
      throw new OntopiaRuntimeException(e2);
    }
  }

  public Object remove(Object key) {
    return remove(key, true);
  }

  public void removeAll(Collection keys) {
    removeAll(keys, true);
  }
  
  // -----------------------------------------------------------------------------
  // EvictableIF
  // -----------------------------------------------------------------------------
  
  // NOTE: following methods used by transactions on commit to evict
  // entries from shared query cache

  public Object remove(Object key, boolean notifyCluster) {
    return cache.remove(key, notifyCluster);
  }
  
  public void removeAll(Collection keys, boolean notifyCluster) {
    cache.removeAll(keys, notifyCluster);
  }  
  
  public void clear(boolean notifyCluster) {
    lru.clear();
    cache.clear(notifyCluster);
  }
  
  // -----------------------------------------------------------------------------
  // Report
  // -----------------------------------------------------------------------------
  
  public void writeReport(java.io.Writer out, boolean dumpCache) throws java.io.IOException {
    cache.writeReport(out, dumpCache);
  }

}

