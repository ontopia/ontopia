
// $Id: TransactionalSoftHashMapIndex.java,v 1.2 2007/09/27 06:36:47 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.utils.SoftHashMapIndex;

/**
 * INTERNAL: 
 */

public class TransactionalSoftHashMapIndex 
  extends SoftHashMapIndex implements TransactionalLookupIndexIF {

  public void removeAll(Collection keys) {
    Iterator iter = keys.iterator();
    while (iter.hasNext()) {
      remove(iter.next());
    }
  }

  public void commit() {    
    // no-op
  }

  public void abort() {
    // no-op
  }

}
