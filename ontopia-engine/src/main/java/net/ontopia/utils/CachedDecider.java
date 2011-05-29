
package net.ontopia.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL: Decider that maintains a cache of decisions made on a set
 * of objects. It works with any implementation of DeciderIF.</p>
 * 
 * The cache is first checked to see if a decision has already been
 * made. Otherwise a new decision is evaluated and the cache is
 * updated.</p>
 */

public class CachedDecider<T> implements DeciderIF<T>, CachedIF {

  protected DeciderIF<T> decider;
  protected Map<T, Boolean> cache = new HashMap<T, Boolean>();
  
  public CachedDecider(DeciderIF<T> decider) {
    this.decider = decider;
  }

  /**
   * Gets the decider that being cached.
   */
  public DeciderIF<T> getDecider() {
    return decider;
  }
  
  /**
   * Sets the decider that is to be cached.
   */
  public void setDecider(DeciderIF<T> decider) {
    this.decider = decider;
  }
  
  public boolean ok(T object) {
    if (object == null) return false;
    if (cache.containsKey(object)) return cache.get(object).booleanValue();
    boolean decision = decider.ok(object);
    if (decision == true)
      cache.put(object, Boolean.TRUE);
    else
      cache.put(object, Boolean.FALSE);      
    return decision;
  }
  
  public void refresh() {
    cache.clear();
  }

}




