
// $Id: EnumerationIterator.java,v 1.7 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: A wrapper class for traversing enumerations as iterators.
 */

public class EnumerationIterator implements Iterator {

  protected Enumeration enumeration;
  
  public EnumerationIterator(Enumeration enumeration) {
    this.enumeration = enumeration;
  }
  
  public boolean hasNext() {
    return enumeration.hasMoreElements();
  }

  public Object next() {
    return enumeration.nextElement();
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}
