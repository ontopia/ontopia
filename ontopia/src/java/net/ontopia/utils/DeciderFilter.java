
// $Id: DeciderFilter.java,v 1.8 2005/04/07 08:42:31 larsga Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Filter that filters a collection using a decider. The
 * decider is applied to the individual objects in the collection. If
 * the object is accepted by the decider it will become part of the
 * result.</p>
 */
public class DeciderFilter implements FilterIF {

  protected DeciderIF decider;

  public DeciderFilter(DeciderIF decider) {
    this.decider = decider;
  }
  
  public Collection filter(Iterator objects) {
    // Initialize result
    List result = new ArrayList();

    // Loop over the objects
    while (objects.hasNext()) {
      Object object = objects.next();
      // Add object to result if accepted by decider
      if (decider.ok(object))
        result.add(object);
    }
    return result;
  }

}
