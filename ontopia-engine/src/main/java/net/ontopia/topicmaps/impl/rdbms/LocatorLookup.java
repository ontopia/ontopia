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
import net.ontopia.utils.LookupIndexIF;
import net.ontopia.utils.NullObject;
import net.ontopia.utils.SoftHashMapIndex;
import org.apache.commons.collections4.map.LRUMap;

/**
 * INTERNAL: Non-shared locator lookup index.
 */
// <LocatorIF, TMObjectIF>
public class LocatorLookup implements TransactionalLookupIndexIF {
  protected String qname;
  protected TransactionIF txn;
  protected TopicMapIF tm;
  protected int lrusize;

  protected LookupIndexIF cache;
  protected Map lru;

  public LocatorLookup(String qname, TransactionIF txn, TopicMapIF tm, int lrusize) {
    this.qname = qname;
    this.txn = txn;
    this.tm = tm;
    this.lrusize = lrusize;
    this.cache = new SoftHashMapIndex();
    this.lru = new LRUMap(lrusize);
  }

  // ISSUE: soft reference string keys or identity values?
  
  public Object get(Object key) {
    // check cache
    Object retval = cache.get(key);
    if (retval == null) {
      // cache miss
      LocatorIF locator = (LocatorIF)key;
      retval = txn.executeQuery(qname, new Object[] { tm, locator.getAddress() });
      // update cache and lru
      cache.put(key, (retval == null ? NullObject.INSTANCE : retval));
      lru.put(key, (retval == null ? NullObject.INSTANCE : retval)); // ISSUE: does it make sense to LRU misses?
      return retval;      
    } else {
      // cache hit
      lru.put(key, retval);
      return (retval == NullObject.INSTANCE ? null : retval);
    }
  }

  public Object put(Object key, Object value) {
    return cache.put(key, value);
  }

  public Object remove(Object key) {
    return cache.remove(key);
  }

  public void removeAll(Collection keys) {
    Iterator iter = keys.iterator();
    while (iter.hasNext()) {
      cache.remove(iter.next());
    }
  }

  public void commit() {    
    // no-op
  }

  public void abort() {
    // no-op
  }

}
