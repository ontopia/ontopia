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

import net.ontopia.persistence.proxy.QueryCache;
import net.ontopia.persistence.proxy.StorageAccessIF;
import net.ontopia.persistence.proxy.TransactionalLookupIndexIF;

/**
 * INTERNAL: Non-shared locator lookup index.
 */

public class SharedQueryLookup implements TransactionalLookupIndexIF {

  protected StorageAccessIF access;
  protected QueryCache qcache;

  public SharedQueryLookup(StorageAccessIF access, QueryCache qcache) {
    this.access = access;
    this.qcache = qcache;
  }
  
  public Object get(Object key) {
    // check cache
    ParameterArray params = (ParameterArray)key;
    return qcache.executeQuery(access, params, params.getArray());
  }

  public Object put(Object key, Object value) {
    throw new UnsupportedOperationException();
  }

  public Object remove(Object key) {
    throw new UnsupportedOperationException();
  }

  public void removeAll(Collection keys) {
    qcache.removeAll(keys);
  }

  public void commit() {    
    // no-op
  }

  public void abort() {
    // no-op
  }

}
