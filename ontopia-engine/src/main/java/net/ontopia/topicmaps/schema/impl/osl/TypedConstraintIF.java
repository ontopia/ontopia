
package net.ontopia.topicmaps.schema.impl.osl;

import net.ontopia.topicmaps.schema.core.ConstraintIF;

/**
 * INTERNAL: Interface implemented by constraints and classes which
 * constrain the type of a topic map object.
 */
public interface TypedConstraintIF extends ConstraintIF {

  /**
   * INTERNAL: Returns the object specifying the allowed types.
   */
  public TypeSpecification getTypeSpecification();

  /**
   * INTERNAL: Sets the object specifying the allowed types.
   */
  public void setTypeSpecification(TypeSpecification spec);
  
}






