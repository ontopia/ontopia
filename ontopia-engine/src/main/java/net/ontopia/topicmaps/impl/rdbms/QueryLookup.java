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
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.ReferenceMap;

/**
 * INTERNAL: Non-shared locator lookup index.
 */
public class QueryLookup<V> implements TransactionalLookupIndexIF<ParameterArray, V> {
  protected String qname;
  protected TransactionIF txn;

  protected Map<ParameterArray, V> cache;
  protected Map<Object, Object> lru;
  private final V NULLOBJECT;

  public QueryLookup(String qname, TransactionIF txn, int lrusize, V nullObject) {
    this.qname = qname;
    this.txn = txn;
    this.cache = new ReferenceMap(AbstractReferenceMap.ReferenceStrength.SOFT, AbstractReferenceMap.ReferenceStrength.HARD);
    this.lru = new LRUMap(lrusize);
	NULLOBJECT = nullObject;
  }

  // ISSUE: soft reference string keys or identity values?
  
  @Override
  public V get(ParameterArray params) {
    // check cache
    V retval = cache.get(params);
    if (retval == null) {
      // cache miss
      retval = (V) txn.executeQuery(qname, params.getArray());
      // update cache and lru
      cache.put(params, (retval == null ? NULLOBJECT : retval));
      lru.put(params, (retval == null ? NULLOBJECT : retval));
      return retval;      
    } else {
      // cache hit
      lru.put(params, retval);
      return (retval == NULLOBJECT ? null : retval);
    }
  }

  @Override
  public V put(ParameterArray key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(ParameterArray key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeAll(Collection<ParameterArray> keys) {
    throw new UnsupportedOperationException();
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
