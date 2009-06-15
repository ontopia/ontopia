// $Id: ContextNameGrabber.java,v 1.10 2008/06/12 14:37:18 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.grabbers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import net.ontopia.utils.DeciderIF;
import net.ontopia.utils.GrabberIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.utils.ScopedIFComparator;
import net.ontopia.topicmaps.utils.IntersectionOfContextDecider;

/**
 * INTERNAL: Grabber that grabs the most appropriate basename from a
 * topic and then the most appropriate variant name, if one can be
 * found. If no better variant name can be found, the base name is
 * used. This class is much used for grabbing display and sort names.
 */
public class ContextNameGrabber implements GrabberIF {

  protected DeciderIF within;
  protected Comparator bnComparator;
  protected Comparator vnComparator;
 
  /**
   * INTERNAL: Creates a grabber; makes the comparators ScopedIFComparator
   *         for the given scopes.
   *
   * @param baseNameContext basename scope;
   *        a collection of TopicIF objects.
   * @param variantNameContext variantname scope;
   *        a collection of TopicIF objects.
   */
  public ContextNameGrabber(Collection baseNameContext,
                            Collection variantNameContext) {
    this.within = new IntersectionOfContextDecider(variantNameContext);
    this.bnComparator = new ScopedIFComparator(baseNameContext);
    this.vnComparator = new ScopedIFComparator(variantNameContext);
  }

  /**
   * INTERNAL: Grabs the most appropriate base name for the given topic,
   * using the comparator established at creation to compare available
   * base names and if a sort variant is available it will be used.
   *
   * @param topic A topic; formally an Object, but must implement TopicIF.
   * @return object of class TopicNameIF or VariantNameIF
   * @exception throws OntopiaRuntimeException if the given topic is
   * not a TopicIF object.
   */
  public Object grab(Object topic) {
    TopicIF mytopic;
    try {
      mytopic = (TopicIF) topic;
    } catch (ClassCastException e) {
      throw new OntopiaRuntimeException(topic + " is not a TopicIF.");
    }

    // --- pick out best basename
    Collection basenames = mytopic.getTopicNames();
    int basenames_size = basenames.size();
    if (basenames_size == 0)
      return null;

    TopicNameIF bestTopicName;
    if (basenames_size == 1)
      // Pull out the only basename
      bestTopicName = (TopicNameIF) CollectionUtils.getFirstElement(basenames);
    else {
      // Sort list of basenames
      Object[] mybasenames = basenames.toArray();
      Arrays.sort(mybasenames, bnComparator);
      // Pull out the first basename
      bestTopicName = (TopicNameIF) mybasenames[0];
    }
    
    // --- pick out best variant name
    Collection variantnames = bestTopicName.getVariants();
    int variantnames_size = variantnames.size();
    // If there is no variant name return bestTopicName
    if (variantnames_size == 0)
      return bestTopicName;
    
    // If there is multiple basenames rank them.
    Object[] myvariantnames = variantnames.toArray();
    if (variantnames_size > 1)
      Arrays.sort(myvariantnames, vnComparator);
    
    // Test that first variant is within scope
    if (within.ok(myvariantnames[0]))
      return myvariantnames[0];
    else
      return bestTopicName;
  }

}
