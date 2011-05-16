// $Id: CachedIF.java,v 1.6 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

/**
 * INTERNAL: Interface that objects containing cached information should
 * implement. This interface makes it possible to refresh the cached
 * objects.</p>
 */

public interface CachedIF {

  /**
   * Refreshes the cache.
   */
  public void refresh();

}




