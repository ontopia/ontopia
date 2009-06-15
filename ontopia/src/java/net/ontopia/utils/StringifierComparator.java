// $Id: StringifierComparator.java,v 1.9 2005/10/16 12:06:05 grove Exp $

package net.ontopia.utils;

import java.util.*;
import net.ontopia.utils.*;

/**
 * INTERNAL: Comparator that stringifies the arguments and compares them
 * using another comparator. DefaultStringifier is the default
 * stringifier.</p>
 */

public class StringifierComparator implements Comparator {
  
  protected Comparator comparator;
  protected StringifierIF stringifier;

  public StringifierComparator() {
    stringifier = new DefaultStringifier();
  }

  public StringifierComparator(StringifierIF stringifier) {
    this.stringifier = stringifier;
  }
  
  public StringifierComparator(StringifierIF stringifier, Comparator comparator) {
    this.stringifier = stringifier;
    this.comparator = comparator;
  }

  /**
   * Gets the comparator which is to be used.
   */
  public Comparator getComparator() {
    return comparator;
  }
  
  /**
   * Sets the comparator which is to be used.
   */
  public void setComparator(Comparator comparator) {
    this.comparator = comparator;
  }
  
  public int compare(Object obj1, Object obj2) {
    if (obj1 == obj2) return 0;

    String name1 = stringifier.toString(obj1);
    String name2 = stringifier.toString(obj2);

    if (name1 == null)
      return (name2 == null ? 0 : 1);
    else
      if (name2 == null)
        return -1;
      else {
        int result = name1.compareToIgnoreCase(name2);
        // If the decision has been made
        if (result != 0 || comparator == null) return result;        
        // Use comparator when equally ranked
        return comparator.compare(obj1, obj2);            
      }
  }
  
}




