// $Id: EvictableIF.java,v 1.2 2007/09/27 08:34:29 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.util.Collection;

import net.ontopia.utils.LookupIndexIF;

/**
 * INTERNAL: Simple interface used by helper objects for invalidation purposes.
 */

public interface EvictableIF {

  public Object remove(Object key, boolean notifyCluster);

  public void removeAll(Collection keys, boolean notifyCluster);

  public void clear(boolean notifyCluster);
  
}
