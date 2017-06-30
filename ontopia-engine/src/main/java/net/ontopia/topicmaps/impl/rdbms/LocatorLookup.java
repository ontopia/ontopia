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

package net.ontopia.topicmaps.impl.rdbms;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.persistence.proxy.TransactionalLookupIndexIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.ReferenceMap;

/**
 * INTERNAL: Non-shared locator lookup index.
 */
// <LocatorIF, TMObjectIF>
public class LocatorLookup<E> implements TransactionalLookupIndexIF<LocatorIF, E> {
  protected String qname;
  protected TransactionIF txn;
  protected TopicMapIF tm;
  protected int lrusize;
  private E NULLOBJECT;

  protected Map<LocatorIF, E> cache;
  protected Map<LocatorIF, E> lru;

  public LocatorLookup(String qname, TransactionIF txn, TopicMapIF tm, int lrusize, E nullObject) {
    this.qname = qname;
    this.txn = txn;
    this.tm = tm;
    this.lrusize = lrusize;
    this.cache = new ReferenceMap<LocatorIF, E>(AbstractReferenceMap.ReferenceStrength.SOFT, AbstractReferenceMap.ReferenceStrength.HARD);
    this.lru = new LRUMap<LocatorIF, E>(lrusize);
    NULLOBJECT = nullObject;
  }

  // ISSUE: soft reference string keys or identity values?
  
  @Override
  public E get(LocatorIF key) {
    // check cache
    E retval = cache.get(key);
    if (retval == null) {
      // cache miss
      retval = (E) txn.executeQuery(qname, new Object[] { tm, key.getAddress() });
      // update cache and lru
      cache.put(key, (retval == null ? NULLOBJECT : retval));
      lru.put(key, (retval == null ? NULLOBJECT : retval)); // ISSUE: does it make sense to LRU misses?
      return retval;      
    } else {
      // cache hit
      lru.put(key, retval);
      return (retval == NULLOBJECT ? null : retval);
    }
  }

  @Override
  public E put(LocatorIF key, E value) {
    return cache.put(key, value);
  }

  @Override
  public E remove(LocatorIF key) {
    return cache.remove(key);
  }

  @Override
  public void removeAll(Collection<LocatorIF> keys) {
    Iterator<LocatorIF> iter = keys.iterator();
    while (iter.hasNext()) {
      cache.remove(iter.next());
    }
  }

  @Override
  public void commit() {    
    // no-op
  }

  @Override
  public void abort() {
    // no-op
  }

}
