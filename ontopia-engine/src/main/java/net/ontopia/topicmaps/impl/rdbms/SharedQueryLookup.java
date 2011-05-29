
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
