
// $Id: LocatorLookup.java,v 1.10 2008/06/04 12:23:09 geir.gronmo Exp $

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

import org.apache.commons.collections.map.LRUMap;

/**
 * INTERNAL: Non-shared locator lookup index.
 */

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
