
// $Id: OccurrenceConstraint.java,v 1.7 2004/11/29 18:44:27 grove Exp $

package net.ontopia.topicmaps.schema.impl.osl;

/**
 * INTERNAL: Represents a constraint on the occurrences of a class of topics.
 */
public class OccurrenceConstraint
                            extends AbstractScopedTypedCardinalityConstraint {
  /**
   * INTERNAL: Used to indicate that occurrences can only be internal, that
   * is be represented using the &lt;resourceData> element in XTM 1.0.
   */
  public static final int RESOURCE_INTERNAL = 0;
  /**
   * INTERNAL: Used to indicate that occurrences can only be external, that
   * is be represented using the URI of the resource.
   */
  public static final int RESOURCE_EXTERNAL = 1;
  /**
   * INTERNAL: Used to indicate that occurrences can be either internal or
   * external.
   */
  public static final int RESOURCE_EITHER   = 2;
  
  protected TopicConstraintCollection parent;
  protected int internal;
  
  /**
   * INTERNAL: Creates a new occurrence constraint in the given
   * collection of topic constraints.
   */
  public OccurrenceConstraint(TopicConstraintCollection parent) {
    this.parent = parent;
    this.internal = RESOURCE_EITHER;
  }

  /**
   * INTERNAL: Used to control whether instances of occurrence type can be
   * internal, external, or both. The allowed values are those of the
   * RESOURCE_* constants.
   */
  public void setInternal(int internal) {
    this.internal = internal;
  }

  /**
   * INTERNAL: Returns a value indicating whether instances of this
   * occurrence type can be internal, external, or both. The allowed
   * values are those of the RESOURCE_* constants.
   */
  public int getInternal() {
    return internal;
  }
  
  // --- Object methods
  
}
