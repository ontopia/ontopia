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

import java.util.Collection;
import java.util.Map;
import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: CacheIF implementation that wraps a Map and notified the
 * cluster about removals. All access is synchronized.
 */

public final class ClusteredCache<K, V> implements CacheIF<K, V> {

  protected final Map<K, V> cache;

  protected ClusterIF cluster;
  protected int cacheType;
  protected IdentityIF namespace;
  
  ClusteredCache(Map<K, V> cache, ClusterIF cluster, int cacheType, IdentityIF namespace) {
    this.cache = cache;
    this.cluster = cluster;
    this.cacheType = cacheType;
    this.namespace = namespace;    
  }

  @Override
  public synchronized V get(K key) {
    return cache.get(key);
  }

  @Override
  public synchronized V put(K key, V value) {
    return cache.put(key, value);
  }
  
  @Override
  public synchronized V remove(K key, boolean notifyCluster) {
    V o = cache.remove(key);
    // notify cluster
    if (notifyCluster) {
      cluster.evictCache(namespace, cacheType, key);
    }
    return o;
  }
  
  @Override
  public synchronized void removeAll(Collection<K> keys, boolean notifyCluster) {
    for (K key : keys) {
      cache.remove(key);
    }
    // notify cluster
    if (notifyCluster) {
      cluster.evictCache(namespace, cacheType, keys);
    }
  }

  @Override
  public synchronized void clear(boolean notifyCluster) {
    cache.clear();
    // notify cluster
    if (notifyCluster) {
      cluster.clearCache(namespace, cacheType);
    }
  }
  
  @Override
  public void writeReport(java.io.Writer out, boolean dumpCache) throws java.io.IOException {
    synchronized (cache) {
      out.write("<p>Cache size: " + cache.size() + "</p>\n");
      
      if (dumpCache) {
        out.write("<table>\n");
        for (K key : cache.keySet()) {
          if (key == null) {
            continue;
          }
          V val = cache.get(key);
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
