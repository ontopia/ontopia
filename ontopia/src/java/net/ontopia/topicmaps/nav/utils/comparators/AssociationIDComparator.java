// $Id: AssociationIDComparator.java,v 1.4 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.comparators;

import java.util.Comparator;
import java.text.Collator;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.AssociationIF;

/**
 * INTERNAL: A Comparator for ordering AssociationIFs after their ID.
 *
 * @since 1.2.5
 */
public class AssociationIDComparator implements Comparator {

  protected Comparator ac;

  /**
   * Default constructor.
   */  
  public AssociationIDComparator() {
    ac = Collator.getInstance();;
  }
  
  /**
   * Compares two AssociationIFs.
   */
  public int compare(Object o1, Object o2) {
    AssociationIF a1, a2;
    try {
      a1 = (AssociationIF) o1;
      a2 = (AssociationIF) o2;
    } catch (ClassCastException e) {
      String msg = "AssociationIDComparator Error: " +
        "This comparator only compares AssociationIFs";
      throw new OntopiaRuntimeException(msg);
    }
    return ac.compare(a1.getObjectId(), a2.getObjectId());
  }
  
}





