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
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import net.ontopia.persistence.query.sql.DetachedQueryIF;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.collections4.map.LRUMap;

/**
 * INTERNAL: A storage access implementation accessing relational
 * databases using JDBC.
 */

public class QueryCache<K, E> implements EvictableIF<K, E> {

  protected DetachedQueryIF query;
  protected CacheIF<K, E> cache;
  protected Map<K, E> lru;
  protected int lrusize;
  private final E NULLOBJECT;
  
  QueryCache(DetachedQueryIF query, CacheIF<K, E> cache, int lrusize, E nullObject) {
    this.query = query;
    this.cache = cache;
    this.lru = Collections.synchronizedMap(new LRUMap<K, E>(lrusize));
    this.lrusize = lrusize;
    NULLOBJECT = nullObject;
  }
  
  public E executeQuery(StorageAccessIF access, K cachekey, Object[] query_params) {
    try {
      E result = cache.get(cachekey);
      if (result == null) {
        // cache miss
        result = (E) query.executeQuery(((RDBMSAccess)access).getConnection(), query_params);
        cache.put(cachekey, (result == null ? NULLOBJECT : result));
        lru.put(cachekey, (result == null ? NULLOBJECT : result));
        return result;
      } else {
        // cache hit
        lru.put(cachekey, result);
        return (Objects.equals(NULLOBJECT, result) ? null : result);
      }
    } catch (RuntimeException e1) {
      throw e1;
    } catch (Exception e2) {
      throw new OntopiaRuntimeException(e2);
    }
  }

  public E remove(K key) {
    return remove(key, true);
  }

  public void removeAll(Collection<K> keys) {
    removeAll(keys, true);
  }
  
  // -----------------------------------------------------------------------------
  // EvictableIF
  // -----------------------------------------------------------------------------
  
  // NOTE: following methods used by transactions on commit to evict
  // entries from shared query cache

  @Override
  public E remove(K key, boolean notifyCluster) {
    return cache.remove(key, notifyCluster);
  }
  
  @Override
  public void removeAll(Collection<K> keys, boolean notifyCluster) {
    cache.removeAll(keys, notifyCluster);
  }  
  
  @Override
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

