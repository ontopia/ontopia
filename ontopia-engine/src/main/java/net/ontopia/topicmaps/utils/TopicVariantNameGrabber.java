
package net.ontopia.topicmaps.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.GrabberIF;

/**
 * INTERNAL: Grabber that grabs the most highest ranked variant name by
 * scope from a topic, ignoring the scope of the base names.</p>
 *
 * The grabber uses a ScopedIFComparator internally to rank all the
 * variant names of the given topic. If the topic has no applicable
 * variant names, null is returned.</p>
 *
 * @since 2.0.3
 */
public class TopicVariantNameGrabber implements GrabberIF<TopicIF, VariantNameIF> {

  /**
   * PROTECTED: The comparator used to sort the variant names.
   */
  protected Comparator<? super VariantNameIF> comparator;
 
  /**
   * INTERNAL: Creates a grabber.
   *
   * @param scope A scope; a collection of TopicIF objects.
   */
  public TopicVariantNameGrabber(Collection<TopicIF> scope) {
    this.comparator = new ScopedIFComparator<VariantNameIF>(scope);
  }

  /**
   * INTERNAL: Creates a grabber which uses the given comparator.
   *
   * @param comparator The given comparator
   */
  public TopicVariantNameGrabber(Comparator<? super VariantNameIF> comparator) {
    this.comparator = comparator;
  }

  /**
   * INTERNAL: Grabs the most appropriate variant name for the given
   * topic name, using the comparator established at creation to
   * compare available variant names.
   *
   * @param topic an object, but must implement TopicIF
   * @return the most applicable variant name, or null.
   * @exception throws OntopiaRuntimeException if the given topic
   *                   is not a TopicIF object.
   */
  public VariantNameIF grab(TopicIF topic) {
    List<VariantNameIF> variants = new ArrayList<VariantNameIF>();

    Iterator<TopicNameIF> it = topic.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF basename = it.next();
      variants.addAll(basename.getVariants());
    }
    
    // If there are no variant names return null
    if (variants.isEmpty())
      return null;

    // If there is multiple variant names rank them.
    Collections.sort(variants, comparator);
    return variants.get(0);
  }
  
}
