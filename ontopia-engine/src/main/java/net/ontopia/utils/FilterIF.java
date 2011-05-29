
package net.ontopia.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * INTERNAL: Filters the objects in an iterator.<p>
 * 
 * Classes implementing the filter method of this interface must
 * return a collection containing a subset of the elements in the
 * iterator given to it.<p>
 */

public interface FilterIF<T> {

  /**
   * INTERNAL: Filters the input iterator and returns a collection
   * containing a subset of the iterator's elements.
   */
  public Collection<T> filter(Iterator<T> objects);
  
}




