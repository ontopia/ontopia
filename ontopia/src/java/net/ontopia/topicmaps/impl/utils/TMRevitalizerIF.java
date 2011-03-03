
// $Id: TMRevitalizerIF.java,v 1.2 2006/04/06 11:40:35 grove Exp $

package net.ontopia.topicmaps.impl.utils;

/**
 * INTERNAL: Interface implemented by a class that is able to
 * revitalize objects.
 */

public interface TMRevitalizerIF {

  /**
   * INTERNAL: Returns a revitalized version of the given object.
   */
  public Object revitalize(Object o);
  
}
