
package net.ontopia.topicmaps.nav.utils.comparators;

import java.util.*;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

/**
 * INTERNAL: A Comparator for ordering locators alphabetically.
 */
public class LocatorComparator implements Comparator {

  protected Comparator tc;
  protected Collection scopes;

  /**
   * Empty constructor, used on application startup to initialise a
   * "fast" comparator which will compare Locators using no context.
   */
  public LocatorComparator() {
    tc = new StringifierComparator();
  }

  /**
   * Constructor used to make a comparator which will compare Locators
   * using the context provided. This implementation doesn't use the
   * context yet.
   */
  public LocatorComparator(Collection context) {
    this.scopes = context;
    tc = new StringifierComparator();
  }
  
  /**
   * Compares two LocatorIFs
   */
  public int compare(Object o1, Object o2) {
    LocatorIF l1, l2;
    try {
      l1 = (LocatorIF) o1;
      l2 = (LocatorIF) o2;
    } catch (ClassCastException e) {
      String msg = "LocatorComparator Error: " +
        "This comparator only compares LocatorIFs";
      throw new OntopiaRuntimeException(msg);
    }
    return tc.compare(l1.getAddress(), l2.getAddress());
  }
}





