
package net.ontopia.topicmaps.schema.impl.osl;

import net.ontopia.topicmaps.schema.core.CardinalityConstraintIF;

/**
 * INTERNAL: Common base class for constraints which have cardinality
 * facets.
 */
public abstract class AbstractCardinalityConstraint
                                        implements CardinalityConstraintIF {
  protected int minimum;
  protected int maximum;
  
  public AbstractCardinalityConstraint() {
    this.minimum = 0;
    this.maximum = CardinalityConstraintIF.INFINITY;
  }

  public int getMinimum() {
    return minimum;
  }

  public int getMaximum() {
    return maximum;
  }

  public void setMinimum(int minimum) {
    if (minimum < 0)
      throw new IllegalArgumentException("Cannot set minimum to negative value");
    this.minimum = minimum;
  }

  public void setMaximum(int maximum) {
    if (maximum != CardinalityConstraintIF.INFINITY && maximum < 0)
      throw new IllegalArgumentException("Cannot set maximum to negative value");
    this.maximum = maximum;
  }
  
}
