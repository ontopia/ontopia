
// $Id: SynchronizedLookupIndex.java,v 1.2 2007/05/29 09:03:27 geir.gronmo Exp $

package net.ontopia.utils;

/**
 * INTERNAL: Synchronized wrapper class for LookupIndexIF instances.
 */
public class SynchronizedLookupIndex implements LookupIndexIF {
  
  protected LookupIndexIF index;

  public SynchronizedLookupIndex(LookupIndexIF index) {
    this.index = index;
  }

  public synchronized Object get(Object key) {
    return index.get(key);
  }

  public synchronized Object put(Object key, Object value) {
    return index.put(key, value);
  }

  public synchronized Object remove(Object key) {
    return index.remove(key);
  }

  public LookupIndexIF getIndex() {
    return index;
  }
  
}
