
package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Grabber that grabs the most highest ranked variant name by
 * scope from a basename.</p>
 *
 * The grabber uses a ScopedIFComparator internally to rank the
 * variant names of the given basename. If the basename has no
 * applicable variant names, null is returned.</p>
 */
public class VariantNameGrabber implements GrabberIF {

  /**
   * PROTECTED: The comparator used to sort the variant names.
   */
  protected Comparator comparator;
 
  /**
   * INTERNAL: Creates a grabber; makes the comparator a ScopedIFComparator
   *         for the given scope.
   *
   * @param scope A scope; a collection of TopicIF objects.
   */
  public VariantNameGrabber(Collection scope) {
    this.comparator = new ScopedIFComparator(scope);
  }

  /**
   * INTERNAL: Creates a grabber which uses the given comparator.
   *
   * @param comparator The given comparator
   */
  public VariantNameGrabber(Comparator comparator) {
    this.comparator = comparator;
  }

  /**
   * INTERNAL: Grabs the most appropriate variant name for the given base
   * name, using the comparator established at creation to compare
   * available variant names.
   *
   * @param basename an object, but must implement TopicNameIF.
   * @return the most applicable variant name, or null.
   * @exception throws OntopiaRuntimeException if the given base name 
   *                   is not a TopicNameIF object.
   */
  public Object grab(Object basename) {
    if (!(basename instanceof TopicNameIF))
      throw new OntopiaRuntimeException(basename + " is not a TopicNameIF");

    TopicNameIF _basename = (TopicNameIF) basename;
    Collection variants = _basename.getVariants();

    // If there are no variant names return the base name itself.
    if (variants.isEmpty())
      return null;

    // If there are multiple variant names rank them.
    Object[] _variants = variants.toArray();
    if (_variants.length > 1)
      Arrays.sort(_variants, comparator);
    return _variants[0];
  }
  
}
