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
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections4.map.LRUMap;

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
  
  @Override
  public Object get(Object key) {
    Object retval = cache.get(key);    
    if (retval != null) {
      lru.put(key, retval);
    }
    return retval;
  }

  @Override
  public Object put(Object key, Object value) {
    lru.put(key, value);
    return cache.put(key, value);
  }

  @Override
  public Object remove(Object key) {
    return remove(key, true);
  }

  @Override
  public void removeAll(Collection keys) {
    removeAll(keys, true);
  }
  
  @Override
  public void commit() {
    // no-op
  }

  @Override
  public void abort() {
    // no-op
  }

  // -----------------------------------------------------------------------------
  // EvictableIF
  // -----------------------------------------------------------------------------
  
  @Override
  public Object remove(Object key, boolean notifyCluster) {
    lru.remove(key);
    return cache.remove(key, notifyCluster);
  }
  
  @Override
  public void removeAll(Collection keys, boolean notifyCluster) {
    Iterator iter = keys.iterator();
    while (iter.hasNext()) {
      lru.remove(iter.next());
    }
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
