
// $Id: ValueAcceptingTagIF.java,v 1.7 2004/11/12 11:25:23 grove Exp $

package net.ontopia.topicmaps.nav2.core;

import java.util.Collection;

/**
 * INTERNAL: Implemented by tags which accept values through some means
 * (for example from value producing and/or manipulating tags).
 */
public interface ValueAcceptingTagIF {

  /**
   * Accepts input collection and make it accessible for tag.
   */
  public void accept(Collection value);

}
