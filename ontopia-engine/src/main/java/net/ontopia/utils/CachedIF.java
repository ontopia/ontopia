
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




