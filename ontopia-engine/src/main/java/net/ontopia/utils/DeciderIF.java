
package net.ontopia.utils;

/**
 * PUBLIC: Interface for classes that decides whether an object is
 * acceptable or not. A decider is the same as a predicate, and can
 * e.g. be used to filter collections.</p>
 */

public interface DeciderIF<T> {

  /**
   * PUBLIC: Returns true if the object is accepted.
   */
  public boolean ok(T object);

}




