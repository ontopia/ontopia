
// $Id: TopicComparator.java,v 1.20 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.comparators;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import net.ontopia.utils.GrabberIF;
import net.ontopia.utils.StringifierIF;

import net.ontopia.topicmaps.nav.utils.grabbers.ContextNameGrabber;
import net.ontopia.topicmaps.nav.utils.stringifiers.ComparatorNameStringifier;

/**
 * INTERNAL: A Comparator for ordering topics alphabetically. Note that
 * it does not look up the 'sort' topic for you, but that this must be
 * provided explicitly to the constructors.
 */
public class TopicComparator implements Comparator {

  protected GrabberIF nameGrabber;
  protected StringifierIF nameStringifier;

  /**
   * Empty constructor, used on application startup to initialise a
   * "fast" comparator which will compare Topics using no context.
   */
  public TopicComparator() {
    this(Collections.EMPTY_SET, Collections.EMPTY_SET);
  }
  
  /**
   * Constructor used to make a comparator which will compare Topics using the 
   * contexts provided. 
   */
  public TopicComparator(Collection baseNameContext) {
    this(baseNameContext, Collections.EMPTY_SET);
  } 
  
  
  /**
   * Constructor used to make a comparator which will compare Topics
   * using the contexts provided. The variantNameContext will
   * generally be a Sort topic if is available. This is the default
   * applied by the application.
   */
  public TopicComparator(Collection baseNameContext, Collection variantNameContext) {
    nameGrabber = new ContextNameGrabber((baseNameContext == null ? Collections.EMPTY_SET :
                                          baseNameContext),
                                         (variantNameContext == null ? Collections.EMPTY_SET :
                                          variantNameContext));
    nameStringifier = new ComparatorNameStringifier();
  }

  /**
   * implementing method which is required for Comparator interface.
   */
  public int compare(Object o1, Object o2) {

    // this method is time-critical, since it is called n*log(n) times
    // for every list of topics. could probably do more to make it
    // faster.

    if (o1 == null)
      return 1;
    if (o2 == null)
      return -1;

    String n1 = nameStringifier.toString(nameGrabber.grab(o1));
    String n2 = nameStringifier.toString(nameGrabber.grab(o2));

    if (n1 == null)
      return 1;
    if (n2 == null)
      return -1;
    
    return n1.compareToIgnoreCase(n2);
  }
  
}
