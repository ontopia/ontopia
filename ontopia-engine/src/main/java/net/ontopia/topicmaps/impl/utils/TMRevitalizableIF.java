
package net.ontopia.topicmaps.impl.utils;

/**
 * INTERNAL: Interface implemented by objects able to revitalize
 * themselves with another topic map transaction.
 */

public interface TMRevitalizableIF {

  /**
   * INTERNAL: 
   */
  public void revitalize(TMRevitalizerIF revitalizer);
  
}
