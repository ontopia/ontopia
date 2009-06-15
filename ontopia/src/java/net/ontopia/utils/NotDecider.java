
// $Id: NotDecider.java,v 1.6 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

/**
 * INTERNAL: Decider that negates the decision of the nested decider.
 */

public class NotDecider implements DeciderIF {

  protected DeciderIF decider;
  
  public NotDecider(DeciderIF decider) {
    this.decider = decider;
  }
  
  public boolean ok(Object object) {
    return !decider.ok(object);
  }

}




