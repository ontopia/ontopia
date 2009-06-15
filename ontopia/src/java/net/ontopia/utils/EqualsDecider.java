// $Id: EqualsDecider.java,v 1.5 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

/**
 * INTERNAL: Decider whether the reference object is equal the given
 * object. The implementation uses the Object.equals method.</p>
 */

public class EqualsDecider implements DeciderIF {

  protected Object refobj;
  
  public EqualsDecider(Object refobj) {
    this.refobj = refobj;
  }
  
  public boolean ok(Object object) {
    return refobj.equals(object);
  }

}




