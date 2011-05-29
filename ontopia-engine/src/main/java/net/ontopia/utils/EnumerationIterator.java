
package net.ontopia.utils;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * INTERNAL: A wrapper class for traversing enumerations as iterators.
 */

public class EnumerationIterator<E> implements Iterator<E> {

  protected Enumeration<E> enumeration;
  
  public EnumerationIterator(Enumeration<E> enumeration) {
    this.enumeration = enumeration;
  }
  
  public boolean hasNext() {
    return enumeration.hasMoreElements();
  }

  public E next() {
    return enumeration.nextElement();
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}
