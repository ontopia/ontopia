// $Id: TopicNameComparator.java,v 1.1 2008/06/12 14:37:17 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.comparators;

import java.util.Collection;
import java.util.Comparator;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * INTERNAL: A Comparator for ordering TopicNameIFs alphabetically
 * (case-independent).
 */
public class TopicNameComparator implements Comparator {

  /**
   * Empty constructor, which will compare TopicNameIFs using no
   * context.
   */
  public TopicNameComparator() {
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
      String msg = "TopicNameComparator Error: " +
        "This comparator only compares TopicNameIFs objects.";
      throw new OntopiaRuntimeException(msg);
    }
    
    if (value1 == null) return 1;
    if (value2 == null) return -1;
    
    return value1.compareToIgnoreCase(value2);
  }
  
}





