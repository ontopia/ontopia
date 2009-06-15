// $Id: VariantConstraint.java,v 1.8 2008/06/12 14:37:23 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Collection;
import java.util.ArrayList;
import net.ontopia.topicmaps.schema.core.CardinalityConstraintIF;

/**
 * INTERNAL: Represents a constraint on the allowed variant names of
 * a base name.
 */
public class VariantConstraint extends AbstractScopedCardinalityConstraint {
  protected TopicNameConstraint parent;
  
  /**
   * INTERNAL: Creates a new variant name constraint belonging to the
   * given base name constraint.
   */
  public VariantConstraint(TopicNameConstraint parent) {
    this.parent = parent;
  }

  /**
   * INTERNAL: Returns the base name constraint that is the parent of
   * this constraint.
   */
  public TopicNameConstraint getParent() {
    return parent;
  }
  
}






