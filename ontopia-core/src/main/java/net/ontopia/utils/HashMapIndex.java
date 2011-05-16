
// $Id: HashMapIndex.java,v 1.4 2002/11/18 13:51:25 larsga Exp $

package net.ontopia.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL: A lookup index that extends HashMap. It never forgets
 * what it is told using put(), but will, if given a fallback object,
 * ask the fallback for values it does not have.
 */

public class HashMapIndex<K, E> extends HashMap<K, E> implements LookupIndexIF<K, E> {

  protected LookupIndexIF<K, E> fallback;

  public HashMapIndex() {
  }
  
  public HashMapIndex(Map<K, E> map) {
    super(map);
  }

  public HashMapIndex(LookupIndexIF<K, E> fallback) {
    this.fallback = fallback;
  }
  
  public HashMapIndex(Map<K, E> map, LookupIndexIF<K, E> fallback) {
    super(map);
    this.fallback = fallback;
  }
  
  public E get(Object key) {
    if (containsKey(key))
      return super.get(key);
    else if (fallback != null)
      return fallback.get((K)key);
    return null;
  }
}
