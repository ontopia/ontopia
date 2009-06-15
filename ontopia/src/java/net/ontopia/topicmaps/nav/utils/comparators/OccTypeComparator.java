// $Id: OccTypeComparator.java,v 1.5 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.comparators;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import net.ontopia.utils.GrabberIF;
import net.ontopia.utils.StringifierIF;
import net.ontopia.utils.DeciderIF;

import net.ontopia.topicmaps.nav.utils.grabbers.ContextNameGrabber;
import net.ontopia.topicmaps.nav.utils.stringifiers.ComparatorNameStringifier;

import net.ontopia.topicmaps.nav2.impl.basic.TypeDecider;

import org.apache.log4j.Logger;

/**
 * INTERNAL: A Comparator for ordering topics alphabetically. Note that
 * it does not look up the 'sort' topic for you, but that this must be
 * provided explicitly to the constructors.
 */
public class OccTypeComparator extends TopicComparator implements Comparator {

  // initialization of logging facility
  private static Logger log = Logger
    .getLogger(OccTypeComparator.class.getName());

  // members
  private static DeciderIF metadataDecider = null;
  private static DeciderIF descriptionDecider = null;

  public OccTypeComparator() {
    super();
    try {
      metadataDecider = new TypeDecider(TypeDecider.OCC_METADATA);
      descriptionDecider = new TypeDecider(TypeDecider.OCC_DESCRIPTION);
    } catch (MalformedURLException mue) {
      log.info("Could not find metadata occurrence type topic");
    }
  }

  public int compare(Object o1, Object o2) {
    if (o1 == null)
      return 1;
    if (o2 == null)
      return -1;

    String n1 = nameStringifier.toString(nameGrabber.grab(o1));
    if (n1 == null)
      return 1;
    // prefix
    if (metadataDecider.ok(o1)) {
      n1 = "META" + n1;
    } else {
      if (descriptionDecider.ok(o1))
        n1 = "ZZZDESC" + n1;
    }

    String n2 = nameStringifier.toString(nameGrabber.grab(o2));
    if (n2 == null)
      return -1;
    // prefix
    if (metadataDecider.ok(o2)) {
      n2 = "META" + n2;
    } else {
      if (descriptionDecider.ok(o2))
        n2 = "ZZZDESC" + n2;
    }
    
    return n1.compareToIgnoreCase(n2);
  }  
  
}





