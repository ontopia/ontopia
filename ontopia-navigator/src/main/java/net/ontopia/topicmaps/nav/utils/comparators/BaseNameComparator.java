
package net.ontopia.topicmaps.nav.utils.comparators;

import java.util.Collection;
import java.util.Comparator;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * DEPRECATED: A Comparator for ordering TopicNameIFs alphabetically
 * (case-independent).
 * @deprecated Use TopicNameComparator instead.
 */
public class BaseNameComparator implements Comparator {

  /**
   * Empty constructor, which will compare TopicNameIFs using no
   * context.
   */
  public BaseNameComparator() {
  }
  
  /**
   * Compares two TopicNameIFs.
   */
  public int compare(Object o1, Object o2) {
    String value1, value2;

    try {
      value1 = ((TopicNameIF) o1).getValue();
      value2 = ((TopicNameIF) o2).getValue();
    } catch (ClassCastException e) {
      String msg = "BaseNameComparator Error: " +
        "This comparator only compares TopicNameIFs objects.";
      throw new OntopiaRuntimeException(msg);
    }
    
    if (value1 == null) return 1;
    if (value2 == null) return -1;
    
    return value1.compareToIgnoreCase(value2);
  }
  
}





