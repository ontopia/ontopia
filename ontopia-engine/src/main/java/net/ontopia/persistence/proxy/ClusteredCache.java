
package net.ontopia.persistence.proxy;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;

import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: CacheIF implementation that wraps a Map and notified the
 * cluster about removals. All access is synchronized.
 */

public final class ClusteredCache implements CacheIF {

  protected Map cache;

  protected ClusterIF cluster;
  protected int cacheType;
  protected IdentityIF namespace;
  
  ClusteredCache(Map cache, ClusterIF cluster, int cacheType, IdentityIF namespace) {
    this.cache = cache;
    this.cluster = cluster;
    this.cacheType = cacheType;
    this.namespace = namespace;    
  }

  public synchronized Object get(Object key) {
    return cache.get(key);
  }

  public synchronized Object put(Object key, Object value) {
    return cache.put(key, value);
  }
  
  public synchronized Object remove(Object key, boolean notifyCluster) {
    Object o = cache.remove(key);
    // notify cluster
    if (notifyCluster)
      cluster.evictCache(namespace, cacheType, key);
    return o;
  }
  
  public synchronized void removeAll(Collection keys, boolean notifyCluster) {
    Iterator iter = keys.iterator();
    while (iter.hasNext()) {
      cache.remove(iter.next());
    }
    // notify cluster
    if (notifyCluster)
      cluster.evictCache(namespace, cacheType, keys);
  }

  public synchronized void clear(boolean notifyCluster) {
    cache.clear();
    // notify cluster
    if (notifyCluster)
      cluster.clearCache(namespace, cacheType);
  }
  
  public void writeReport(java.io.Writer out, boolean dumpCache) throws java.io.IOException {
    synchronized (cache) {
      out.write("<p>Cache size: " + cache.size() + "</p>\n");
      
      if (dumpCache) {
        out.write("<table>\n");
        Iterator iter = cache.keySet().iterator();
        while (iter.hasNext()) {
          Object key = iter.next();
          if (key == null) continue;
          Object val = cache.get(key);
          out.write("<tr><td>");
          out.write((key == null ? "null" : StringUtils.escapeHTMLEntities(key.toString())));
          out.write("</td><td>");
          out.write((val == null ? "null" : StringUtils.escapeHTMLEntities(val.toString())));
          out.write("</td></tr>\n");
        }
        out.write("</table><br>\n");
      }
    }
  }

}
