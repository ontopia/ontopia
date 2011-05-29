
package net.ontopia.utils;

/**
 * INTERNAL: A marker interface implemented by objects which can be
 * cleared. For the most part this interface is implemented by
 * LookupIndexIFs that actually can clear themselves.
 */

public interface ClearableIF {

  public void clear();
  
}
