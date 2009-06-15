
// $Id: AndDecider.java,v 1.9 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Decider that checks all subdeciders and returns true if all
 * of them gives a positive decision. Note that the decision is
 * shortcircuited when the first decider gives a negative decision,
 * the rest is then not checked.</p>
 */

public class AndDecider implements DeciderIF {

  protected Collection deciders = new HashSet();

  public AndDecider(Collection deciders) {
    this.deciders = deciders;
  }

  /**
   * Gets the subdeciders.
   */
  public Collection getDeciders() {
    return deciders;
  }

  /**
   * Add a subdecider.
   */
  public void addDecider(DeciderIF decider) {
    deciders.add(decider);
  }

  /**
   * Remove a subdecider.
   */
  public void removeDecider(DeciderIF decider) {
    deciders.remove(decider);
  }
  
  public boolean ok(Object object) {
    Iterator iter = deciders.iterator();
    while (iter.hasNext()) {
      DeciderIF decider = (DeciderIF)iter.next();
        if (!decider.ok(object)) return false;
    }
    return true;
  }

}
