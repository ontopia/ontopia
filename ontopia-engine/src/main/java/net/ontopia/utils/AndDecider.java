
package net.ontopia.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * INTERNAL: Decider that checks all subdeciders and returns true if all
 * of them gives a positive decision. Note that the decision is
 * shortcircuited when the first decider gives a negative decision,
 * the rest is then not checked.</p>
 */

public class AndDecider<T> implements DeciderIF<T> {

  protected Collection<DeciderIF<T>> deciders = new HashSet<DeciderIF<T>>();

  public AndDecider(Collection<DeciderIF<T>> deciders) {
    this.deciders = deciders;
  }

  /**
   * Gets the subdeciders.
   */
  public Collection<DeciderIF<T>> getDeciders() {
    return deciders;
  }

  /**
   * Add a subdecider.
   */
  public void addDecider(DeciderIF<T> decider) {
    deciders.add(decider);
  }

  /**
   * Remove a subdecider.
   */
  public void removeDecider(DeciderIF<T> decider) {
    deciders.remove(decider);
  }
  
  public boolean ok(T object) {
    Iterator<DeciderIF<T>> iter = deciders.iterator();
    while (iter.hasNext()) {
      DeciderIF<T> decider = iter.next();
        if (!decider.ok(object)) return false;
    }
    return true;
  }

}
