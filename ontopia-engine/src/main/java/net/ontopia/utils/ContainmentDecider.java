
package net.ontopia.utils;

import java.util.Collection;
import java.util.HashSet;

/**
 * INTERNAL: Decider that returns true if the object is contained in
 * the referenced collection.
 *
 * @since 4.0
 */

public class ContainmentDecider<T> implements DeciderIF<T> {

    Collection<T> objects = new HashSet<T>();
    
    public ContainmentDecider(Collection<T> objects) {
      this.objects = objects;
    }
    
    public boolean ok(T o) {
      return objects.contains(o);
    }

}




