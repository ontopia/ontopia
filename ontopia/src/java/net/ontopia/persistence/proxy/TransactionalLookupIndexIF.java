// $Id: TransactionalLookupIndexIF.java,v 1.2 2007/09/27 06:36:47 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.util.Collection;
import net.ontopia.utils.LookupIndexIF;

/**
 * INTERNAL: Interface shared by the lookup indexes that look up data
 * in the backend storage.
 */

public interface TransactionalLookupIndexIF extends LookupIndexIF {

  public void removeAll(Collection keys);
  
  public void commit();

  public void abort();

}
