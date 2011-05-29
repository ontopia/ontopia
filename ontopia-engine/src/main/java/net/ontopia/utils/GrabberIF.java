
package net.ontopia.utils;

/**
 * INTERNAL: Grabs an object from another object.</p>
 * 
 * The object that is grabbed decided by the implementation of this
 * interface.</p>
 */

public interface GrabberIF<O, G> {

  /**
   * Returns an object that is somehow extracted from the given
   * object.
   */
  public G grab(O object);

}




