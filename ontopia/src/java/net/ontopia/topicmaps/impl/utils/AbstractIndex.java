// $Id: AbstractIndex.java,v 1.4 2005/07/11 12:26:22 grove Exp $

package net.ontopia.topicmaps.impl.utils;

import net.ontopia.topicmaps.core.index.IndexIF;

/**
 * INTERNAL: An abstract index class.
 */

public abstract class AbstractIndex implements IndexIF {

  /**
   * INTERNAL: Method used by IndexManagerIF to manage index creation. The idea
   * behind this method is for the index itself to decide whether to create a
   * new instance every time or the same one.
   * 
   * @return Index instance.
   */
  public abstract IndexIF getIndex();

}
