
package net.ontopia.utils;

/**
 * INTERNAL: An interface implemented by objects which can be used to
 * look up information, but which can do no more. A simplified version
 * of the Map interface, used for lookup.
 */
public interface LookupIndexIF<K, E> {

  public E get(K key);

  public E put(K key, E value);

  public E remove(K key);
  
}
