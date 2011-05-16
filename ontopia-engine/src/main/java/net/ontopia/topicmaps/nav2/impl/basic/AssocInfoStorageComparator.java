// $Id: AssocInfoStorageComparator.java,v 1.5 2002/05/29 13:38:41 hca Exp $

package net.ontopia.topicmaps.nav2.impl.basic;

import java.util.Comparator;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Specialized comparator for AssocInfoStorage objects.
 */
public class AssocInfoStorageComparator implements Comparator {

  /**
   * INTERNAL: Default constructor.
   */
  public AssocInfoStorageComparator() {
  }

  /**
   * INTERNAL: Compares two AssocInfoStorage objects.
   */
  public int compare(Object o1, Object o2) {
    String value1, value2;

    try {
      value1 = ((AssocInfoStorage) o1).getSortName();
      value2 = ((AssocInfoStorage) o2).getSortName();

    } catch (ClassCastException e) {
      String msg = "AssocInfoStorageComparator Error: " +
        "This comparator only compares AssocInfoStorage objects.";
      throw new OntopiaRuntimeException(msg);
    }

    if (value1 == null) return 1;
    if (value2 == null) return -1;
    
    return value1.compareToIgnoreCase(value2);
  }
  
}





