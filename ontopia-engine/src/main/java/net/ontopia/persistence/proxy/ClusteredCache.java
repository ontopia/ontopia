/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.persistence.proxy;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;

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
