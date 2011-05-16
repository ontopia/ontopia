// $Id: TypedConstraintIF.java,v 1.7 2004/11/29 18:44:27 grove Exp $

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






