// $Id: CachedDecider.java,v 1.6 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Decider that maintains a cache of decisions made on a set
 * of objects. It works with any implementation of DeciderIF.</p>
 * 
 * The cache is first checked to see if a decision has already been
 * made. Otherwise a new decision is evaluated and the cache is
 * updated.</p>
 */

public class CachedDecider implements DeciderIF, CachedIF {

  protected DeciderIF decider;
  protected Map cache = new HashMap();
  
  public CachedDecider(DeciderIF decider) {
    this.decider = decider;
  }

  /**
   * Gets the decider that being cached.
   */
  public DeciderIF getDecider() {
    return decider;
  }
  
  /**
   * Sets the decider that is to be cached.
   */
  public void setDecider(DeciderIF decider) {
    this.decider = decider;
  }
  
  public boolean ok(Object object) {
    if (object == null) return false;
    if (cache.containsKey(object)) return ((Boolean)cache.get(object)).booleanValue();
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




