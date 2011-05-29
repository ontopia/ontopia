
package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

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
public class TopicVariantNameGrabber implements GrabberIF {

  /**
   * PROTECTED: The comparator used to sort the variant names.
   */
  protected Comparator comparator;
 
  /**
   * INTERNAL: Creates a grabber.
   *
   * @param scope A scope; a collection of TopicIF objects.
   */
  public TopicVariantNameGrabber(Collection scope) {
    this.comparator = new ScopedIFComparator(scope);
  }

  /**
   * INTERNAL: Creates a grabber which uses the given comparator.
   *
   * @param comparator The given comparator
   */
  public TopicVariantNameGrabber(Comparator comparator) {
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
  public Object grab(Object topic) {
    if (!(topic instanceof TopicIF))
      throw new OntopiaRuntimeException(topic + " is not a TopicIF");
    
    TopicIF _topic = (TopicIF) topic;
    Collection variants = new ArrayList();

    Iterator it = _topic.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF basename = (TopicNameIF) it.next();
      variants.addAll(basename.getVariants());
    }
    
    // If there are no variant names return null
    if (variants.isEmpty())
      return null;

    // If there is multiple variant names rank them.
    Object[] _variants = variants.toArray();
    if (_variants.length > 1)
      Arrays.sort(_variants, comparator);
    return _variants[0];
  }
  
}
