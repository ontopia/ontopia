// $Id: ScopedConstraintIF.java,v 1.7 2004/11/29 18:44:27 grove Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import net.ontopia.topicmaps.schema.core.ConstraintIF;

/**
 * INTERNAL: Interface implemented by all constraints which constrain
 * the scopes of topic map constructs.
 */
public interface ScopedConstraintIF extends ConstraintIF {

  /**
   * INTERNAL: Returns the object containing the scope specification.
   */
  public ScopeSpecification getScopeSpecification();

  /**
   * INTERNAL: Sets the object containing the scope specification.
   */
  public void setScopeSpecification(ScopeSpecification scope);
  
}






