
// $Id: TMRevitalizableIF.java,v 1.2 2006/04/06 07:45:37 grove Exp $

package net.ontopia.topicmaps.impl.utils;

import net.ontopia.topicmaps.core.TopicMapIF;

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
