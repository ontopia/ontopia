
// $Id: TopicNameConstraint.java,v 1.1 2008/06/12 14:37:23 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Collection;
import java.util.ArrayList;
import net.ontopia.topicmaps.schema.core.CardinalityConstraintIF;

/**
 * INTERNAL: Represents a constraint on a base name within a topic class
 * definition.
 */
public class TopicNameConstraint extends AbstractScopedCardinalityConstraint {
  protected TopicConstraintCollection parent;
  protected Collection variants;
  
  /**
   * INTERNAL: Creates a base name constraint.
   */
  public TopicNameConstraint(TopicConstraintCollection parent) {
    this.parent = parent;
    this.variants = new ArrayList();
  }

  /**
   * INTERNAL: Returns the constraints on the variants of this base name.
   * @return A collection of VariantConstraint objects.
   */
  public Collection getVariantConstraints() {
    return variants;
  }

  /**
   * INTERNAL: Removes the variant constraint from this base name. If
   * the variant constraint is not already registered with this base
   * name constraint the call is ignored.
   */
  public void removeVariantConstraint(VariantConstraint variant) {
    variants.remove(variant);
  }

  /**
   * INTERNAL: Adds the variant constraint to this base name. If the
   * variant constraint is already registered with this base name
   * constraint the call is ignored.
   */
  public void addVariantConstraint(VariantConstraint variant) {
    variants.add(variant);
  }
  
}
