
package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.*;

/**
 * INTERNAL: Comparator that compares object ids of TMObjectIF objects.
 */

public class ObjectIdComparator implements Comparator {

  public static final ObjectIdComparator INSTANCE = new ObjectIdComparator();

   /**
   * INTERNAL: compares the object ids of the given objects
   *
   * @param obj1 object; internally typecast to TMObjectIF
   * @param obj2 object; internally typecast to TMObjectIF
   * @return int; 0 if the two objects have the same object id; otherwise positive/negative
   *        according to compareTo on the (string) values of the object ids
   */ 

  public int compare(Object obj1, Object obj2) {
    return ((TMObjectIF)obj1).getObjectId().compareTo(((TMObjectIF)obj2).getObjectId());
  }
  
}





