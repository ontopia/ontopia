// $Id: WeakHashMapIndex.java,v 1.3 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: A lookup index that extends WeakHashMap. It forgets what
 * it is told using put() at the will of the garbage collector, but
 * will, if given a fallback object, ask the fallback for values it
 * does not have.
 */

public class WeakHashMapIndex extends WeakHashMap implements LookupIndexIF {

  protected LookupIndexIF fallback;

  public WeakHashMapIndex() {
  }
  
  public WeakHashMapIndex(Map map) {
    putAll(map);
  }

  public WeakHashMapIndex(LookupIndexIF fallback) {
    this.fallback = fallback;
  }
  
  public WeakHashMapIndex(Map map, LookupIndexIF fallback) {
    putAll(map);
    this.fallback = fallback;
  }
  
  public Object get(Object key) {
    if (containsKey(key))
      return super.get(key);
    else if (fallback != null)
      return fallback.get(key);
    else
      return null;
  }
}




