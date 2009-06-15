// $Id: IteratorComparator.java,v 1.1 2005/03/17 17:05:31 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Comparator for Iterators.  Compares each element in turn
 * until the end of one Iterator is reached.  Then, if one Iterator
 * contains fewer elements than the other, it is ordered first.
 */

public class IteratorComparator implements Comparator {
  Comparator elementComparator;
  
  public IteratorComparator (Comparator elementComparator) {
    this.elementComparator = elementComparator;
  }
  
  public int compare(Object o1, Object o2) {
    int retVal = 0;
    
    Iterator it1 = (Iterator)o1;
    Iterator it2 = (Iterator)o2;
    
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
