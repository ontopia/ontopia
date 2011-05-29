
package net.ontopia.persistence.proxy;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.utils.LookupIndexIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.SoftHashMapIndex;
import net.ontopia.utils.SynchronizedLookupIndex;

import org.apache.commons.collections.map.LRUMap;

/**
 * INTERNAL: 
 */

public class TransactionalLRULookupIndex implements TransactionalLookupIndexIF, EvictableIF {

  protected CacheIF cache;
  protected Map lru;
  protected int lrusize;

  public TransactionalLRULookupIndex(CacheIF cache, int lrusize) {
    this.cache = cache;
    this.lru = Collections.synchronizedMap(new LRUMap(lrusize));
    this.lrusize = lrusize;
  }
  
  public Object get(Object key) {
    Object retval = cache.get(key);    
    if (retval != null) lru.put(key, retval);
    return retval;
  }

  public Object put(Object key, Object value) {
    lru.put(key, value);
    return cache.put(key, value);
  }

  public Object remove(Object key) {
    return remove(key, true);
  }

  public void removeAll(Collection keys) {
    removeAll(keys, true);
  }
  
  public void commit() {
  }

  public void abort() {
  }

  // -----------------------------------------------------------------------------
  // EvictableIF
  // -----------------------------------------------------------------------------
  
  public Object remove(Object key, boolean notifyCluster) {
    lru.remove(key);
    return cache.remove(key, notifyCluster);
  }
  
  public void removeAll(Collection keys, boolean notifyCluster) {
    Iterator iter = keys.iterator();
    while (iter.hasNext()) {
      lru.remove(iter.next());
    }
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
