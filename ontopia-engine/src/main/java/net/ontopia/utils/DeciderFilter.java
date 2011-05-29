
package net.ontopia.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * INTERNAL: Filter that filters a collection using a decider. The
 * decider is applied to the individual objects in the collection. If
 * the object is accepted by the decider it will become part of the
 * result.</p>
 */
public class DeciderFilter<T> implements FilterIF<T> {

  protected DeciderIF<T> decider;

  public DeciderFilter(DeciderIF<T> decider) {
    this.decider = decider;
  }
  
  public Collection<T> filter(Iterator<T> objects) {
    // Initialize result
    List<T> result = new ArrayList<T>();

    // Loop over the objects
    while (objects.hasNext()) {
      T object = objects.next();
      // Add object to result if accepted by decider
      if (decider.ok(object))
        result.add(object);
    }
    return result;
  }

}
