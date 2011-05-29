
package net.ontopia.persistence.proxy;

import java.util.Collection;

import net.ontopia.utils.LookupIndexIF;

/**
 * INTERNAL: Simple interface used by innermost caches.
 */

public interface CacheIF {

  public Object get(Object key);

  public Object put(Object key, Object value);

  public Object remove(Object key, boolean notifyCluster);

  public void removeAll(Collection keys, boolean notifyCluster);

  public void clear(boolean notifyCluster);

  public void writeReport(java.io.Writer out, boolean dumpCache) throws java.io.IOException;
  
}
