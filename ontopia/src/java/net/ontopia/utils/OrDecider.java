// $Id: OrDecider.java,v 1.7 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Decider that checks all subdeciders and returns true of one
 * of them gives a positive decision.</p>
 *
 * Note that the decision is shortcircuited when the first decider
 * gives a positive decision, the rest is then not checked.</p>
 */

public class OrDecider implements DeciderIF {

  protected Set deciders = new HashSet();
  
  public OrDecider(DeciderIF decider) {
    this.deciders.add(decider);
  }

  public OrDecider(Set deciders) {
    this.deciders = deciders;
  }

  /**
   * Gets the subdeciders.
   */
  public Set getDeciders() {
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
        if (decider.ok(object)) return true;
    }
    return false;
  }

}




