
package net.ontopia.topicmaps.schema.core;

import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * PUBLIC: Represents a constraint specified in a schema.
 */
public interface ConstraintIF {

  /**
   * PUBLIC: Returns true if the object given is constrained by this
   * constraint.
   */
  public boolean matches(TMObjectIF object);
  
}





