
package net.ontopia.utils;

import java.util.Comparator;
import java.util.Iterator;

/**
 * INTERNAL: Comparator for Iterators.  Compares each element in turn
 * until the end of one Iterator is reached.  Then, if one Iterator
 * contains fewer elements than the other, it is ordered first.
 */

public class IteratorComparator<T> implements Comparator<Iterator<T>> {
  Comparator<T> elementComparator;
  
  public IteratorComparator (Comparator<T> elementComparator) {
    this.elementComparator = elementComparator;
  }
  
  public int compare(Iterator<T> it1, Iterator<T> it2) {
    int retVal = 0;
    
    // Iterate until difference is is found or reached end of one or both
    // iterators.
    while (retVal == 0 && it1.hasNext() && it2.hasNext())
      retVal = elementComparator.compare(it1.next(), it2.next());
    
    // (Only) it1 has elements left, so it1 > it2.
    if (retVal == 0 && it1.hasNext())
      retVal = 1;
    
    // (Only) it2 has elements left, so it1 < it2.
    if (retVal == 0 && it2.hasNext())
      retVal = -1;
    return retVal;
  }

}
