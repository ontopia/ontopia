// $Id: StringifierDecider.java,v 1.5 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Decider that stringifies an object and passes it to the
 * subdecider.
 */

public class StringifierDecider implements DeciderIF {

  protected StringifierIF stringifier;
  protected DeciderIF decider;
   
  public StringifierDecider(StringifierIF stringifier, DeciderIF decider) {
    this.stringifier = stringifier;
    this.decider = decider;
  }

  public boolean ok(Object object) {
    return decider.ok(stringifier.toString(object));
  }
  
}




