// $Id: GrabberDecider.java,v 1.6 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Decider that grabs an object and passes it to the
 * subdecider.
 */

public class GrabberDecider implements DeciderIF {

  protected GrabberIF grabber;
  protected DeciderIF decider;
   
  public GrabberDecider(GrabberIF grabber, DeciderIF decider) {
    this.grabber = grabber;
    this.decider = decider;
  }

  public boolean ok(Object object) {
    return decider.ok(grabber.grab(object));
  }
  
}




