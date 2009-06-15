// $Id: DisplayNameGrabber.java,v 1.9 2008/06/12 14:37:23 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * PUBLIC: Grabber that grabs the name most suitable for display from
 * a topic.  If the topic has a display name that will be chosen. If
 * not, the base name in the least constrained scope will be chosen.
 * @deprecated Since 1.1. Use TopicCharacteristicGrabbers instead.
 */

public class DisplayNameGrabber implements GrabberIF {
  /**
   * PROTECTED: The NameGrabber used to implement the grabbing.
   */
  protected GrabberIF subGrabber;
 
  /**
   * PUBLIC: Creates the grabber and sets the comparator to be a 
   * ScopedIFComparator using the least constrained scope.
   */ 
  public DisplayNameGrabber() {
    subGrabber = new NameGrabber(PSI.getXTMDisplay());
  }
  
  /**
   * PUBLIC: Grabs the name for display. The name returned is the
   * first display (variant) name found, when the basenames of the
   * give topic have been sorted using the comparator. If there is no
   * display name, then the last base name found is returned,
   * corresponding to the least constrained scope.
   *
   * @param object The topic whose name is being grabbed; formally an object.
   * @return A name to display; an object implementing TopicNameIF or
   * VariantNameIF, null if the topic has no basenames.
   * @exception Throws OntopiaRuntimeException if object is not a topic.
   */
  public Object grab(Object object) {
    return subGrabber.grab(object);
  }

}





