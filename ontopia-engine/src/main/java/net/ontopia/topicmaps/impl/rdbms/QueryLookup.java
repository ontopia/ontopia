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
import java.util.Map;

import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.persistence.proxy.TransactionalLookupIndexIF;
import net.ontopia.utils.LookupIndexIF;
import net.ontopia.utils.NullObject;
import net.ontopia.utils.SoftHashMapIndex;

import org.apache.commons.collections.map.LRUMap;

/**
 * INTERNAL: Non-shared locator lookup index.
 */
public class QueryLookup implements TransactionalLookupIndexIF {
  protected String qname;
  protected TransactionIF txn;

  protected LookupIndexIF cache;
  protected Map lru;

  public QueryLookup(String qname, TransactionIF txn, int lrusize) {
    this.qname = qname;
    this.txn = txn;
    this.cache = new SoftHashMapIndex();
    this.lru = new LRUMap(lrusize);
  }

  // ISSUE: soft reference string keys or identity values?
  
  public Object get(Object key) {
    // check cache
    ParameterArray params = (ParameterArray)key;
    Object retval = cache.get(params);
    if (retval == null) {
      // cache miss
      retval = txn.executeQuery(qname, params.getArray());
      // update cache and lru
      cache.put(params, (retval == null ? NullObject.INSTANCE : retval));
      lru.put(params, (retval == null ? NullObject.INSTANCE : retval));
      return retval;      
    } else {
      // cache hit
      lru.put(params, retval);
      return (retval == NullObject.INSTANCE ? null : retval);
    }
  }

  public Object put(Object key, Object value) {
    throw new UnsupportedOperationException();
  }

  public Object remove(Object key) {
    throw new UnsupportedOperationException();
  }

  public void removeAll(Collection keys) {
    throw new UnsupportedOperationException();
  }

  public void commit() {    
    // no-op
  }

  public void abort() {
    // no-op
  }

}
