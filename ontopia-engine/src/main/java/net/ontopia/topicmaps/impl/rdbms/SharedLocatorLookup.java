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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.QueryCache;
import net.ontopia.persistence.proxy.StorageAccessIF;
import net.ontopia.persistence.proxy.TransactionalLookupIndexIF;

/**
 * INTERNAL: Non-shared locator lookup index.
 */

public class SharedLocatorLookup implements TransactionalLookupIndexIF {

  protected StorageAccessIF access;
  protected QueryCache qcache;
  protected IdentityIF tmid;

  protected Map txnadd;
  protected Set txnrem;

  public SharedLocatorLookup(StorageAccessIF access, QueryCache qcache, IdentityIF tmid) {
    this.access = access;
    this.qcache = qcache;
    this.tmid = tmid;
    this.txnadd = new HashMap();
    this.txnrem = new HashSet();
  }
  
  public Object get(Object key) {
    // if added return added
    Object retval = txnadd.get(key);
    if (retval != null) return retval;

    // if removed returned null
    if (txnrem.contains(key)) return null;

    // check cache
    LocatorIF locator = (LocatorIF)key;
    return qcache.executeQuery(access, key, new Object[] { tmid, locator.getAddress() });
  }

  public Object put(Object key, Object value) {
    txnrem.remove(key);
    txnadd.put(key, value);
    return null;
  }

  public Object remove(Object key) {
    txnrem.add(key);    
    txnadd.remove(key);
    return null;
  }

  public void removeAll(Collection keys) {
    Iterator iter = keys.iterator();
    while (iter.hasNext()) {
      Object key = iter.next();
      txnrem.add(key);    
      txnadd.remove(key);
    }
  }
  
  public void commit() {    
    // invalidate shared query cache
    if (!txnrem.isEmpty()) {
      try {
        qcache.removeAll(txnrem);
      } finally {
        txnrem = new HashSet();
      }
    }
    if (!txnadd.isEmpty()) {
      try {
        qcache.removeAll(new ArrayList(txnadd.keySet()));
      } finally {
        txnadd = new HashMap();
      }
    }
  }

  public void abort() {
    // reset tracking
    if (!txnadd.isEmpty()) txnadd = new HashMap();
    if (!txnrem.isEmpty()) txnrem = new HashSet();
  }

}
