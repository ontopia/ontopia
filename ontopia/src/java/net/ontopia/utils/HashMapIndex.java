
// $Id: HashMapIndex.java,v 1.4 2002/11/18 13:51:25 larsga Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: A lookup index that extends HashMap. It never forgets
 * what it is told using put(), but will, if given a fallback object,
 * ask the fallback for values it does not have.
 */

public class HashMapIndex extends HashMap implements LookupIndexIF {

  protected LookupIndexIF fallback;

  public HashMapIndex() {
  }
  
  public HashMapIndex(Map map) {
    super(map);
  }

  public HashMapIndex(LookupIndexIF fallback) {
    this.fallback = fallback;
  }
  
  public HashMapIndex(Map map, LookupIndexIF fallback) {
    super(map);
    this.fallback = fallback;
  }
  
  public Object get(Object key) {
    if (containsKey(key))
      return super.get(key);
    else if (fallback != null)
      return fallback.get(key);
    return null;
  }
}
