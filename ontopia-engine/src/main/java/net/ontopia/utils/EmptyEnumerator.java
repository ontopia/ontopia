
package net.ontopia.utils;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * INTERNAL: Implements an Enumeration that contains nothing.
 */
public class EmptyEnumerator implements Enumeration<Object> {
  public static final Enumeration<Object> ENUMERATOR = new EmptyEnumerator();
  
  public boolean hasMoreElements() {
    return false;
  }

  public Object nextElement() {
    throw new NoSuchElementException();
  }
  
}
