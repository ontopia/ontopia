// $Id: TMObjectMatcherIF.java,v 1.5 2004/11/29 18:44:27 grove Exp $

package net.ontopia.topicmaps.schema.core;

import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * INTERNAL: Implementations of this interface can match topic map objects
 * independently of any specific topic map.
 */ 
public interface TMObjectMatcherIF {

  /**
   * INTERNAL: Returns true if this object is matched by the matcher.
   */
  public boolean matches(TMObjectIF object);

  /**
   * INTERNAL: Returns true if this object equals the given parameter.
   */
  public boolean equals(TMObjectMatcherIF object);
}





