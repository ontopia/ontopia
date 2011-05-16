
package net.ontopia.persistence.proxy;

import java.util.Collection;
import net.ontopia.utils.LookupIndexIF;

/**
 * INTERNAL: Interface shared by the lookup indexes that look up data
 * in the backend storage.
 */
public interface TransactionalLookupIndexIF<K, E> extends LookupIndexIF<K, E> {

  public void removeAll(Collection<K> keys);
  
  public void commit();

  public void abort();

}
