// $Id: AssociationComparator.java,v 1.14 2008/06/12 14:37:17 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.comparators;

import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: A Comparator for ordering AssociationIFs alphabetically
 * after their type.
 */
public class AssociationComparator implements Comparator {

  protected Comparator tc;
  protected Collection scopes;

  /**
   * Empty constructor, used on application startup to initialise a
   * "fast" comparator which will compare Associations using no
   * context.
   */  
  public AssociationComparator() {
    StringifierIF bts =
      new GrabberStringifier(new TopicNameGrabber(Collections.EMPTY_LIST),
                             new NameStringifier());
    tc = new StringifierComparator(new GrabberStringifier(new GrabberGrabber(new StringifierGrabber(bts), new UpperCaseGrabber())));
  }

  /**
   * Constructor used to make a comparator which will compare
   * Associations using the context provided.
   */
  public AssociationComparator(Collection context) {
    this.scopes = context;
    if (scopes == null)
      scopes = Collections.EMPTY_LIST;
    StringifierIF bts = new GrabberStringifier(new TopicNameGrabber(scopes),
                                               new NameStringifier());
    tc = new StringifierComparator(new GrabberStringifier(new GrabberGrabber(new StringifierGrabber(bts), new UpperCaseGrabber())));
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
      String msg = "AssociationComparator Error: " +
        "This comparator only compares AssociationIFs";
      throw new OntopiaRuntimeException(msg);
    }
    return tc.compare(a1.getType(), a2.getType());
  }
  
}





