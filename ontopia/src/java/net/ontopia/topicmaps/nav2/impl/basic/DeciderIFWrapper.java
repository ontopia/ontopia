// $Id: DeciderIFWrapper.java,v 1.6 2004/11/12 11:25:35 grove Exp $

package net.ontopia.topicmaps.nav2.impl.basic;

import net.ontopia.utils.DeciderIF;
import net.ontopia.topicmaps.nav2.core.NavigatorDeciderIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A NavigatorDeciderIF implementation which wraps DeciderIF,
 * so that they can work with the navigator decider interface.
 */
public class DeciderIFWrapper implements NavigatorDeciderIF {

  protected DeciderIF decider;
  
  /**
   * INTERNAL: Default constructor.
   */
  public DeciderIFWrapper(DeciderIF decider) {
    this.decider = decider;
  }
    
  // -----------------------------------------------------------
  // Implementation of NavigatorDeciderIF
  // -----------------------------------------------------------

  public boolean ok(NavigatorPageIF contextTag, Object obj) {
    return decider.ok(obj);
  }
  
}





