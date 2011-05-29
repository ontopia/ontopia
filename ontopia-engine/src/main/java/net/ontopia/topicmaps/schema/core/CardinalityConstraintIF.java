
package net.ontopia.topicmaps.schema.core;

/**
 * INTERNAL: Represents constraints that may only match a specified
 * number of objects.
 */
public interface CardinalityConstraintIF extends ConstraintIF {
  public static final int INFINITY = -1;

  /**
   * INTERNAL: Returns the minimum number of objects that the constraint
   * must match.
   */
  public int getMinimum();

  /**
   * INTERNAL: Returns the maximum number of objects that the constraint
   * may match.
   */
  public int getMaximum();

  /**
   * INTERNAL: Sets the minimum number of objects that the constraint
   * must match.
   */
  public void setMinimum(int minimum);

  /**
   * INTERNAL: Sets the maximum number of objects that the constraint
   * may match.
   */
  public void setMaximum(int maximum);
  
}





