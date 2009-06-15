// $Id: ContainmentDecider.java,v 1.2 2008/05/29 10:55:00 geir.gronmo Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Decider that returns true if the object is contained in
 * the referenced collection.
 *
 * @since 4.0
 */

public class ContainmentDecider implements DeciderIF {

    Collection objects = new HashSet();
    
    public ContainmentDecider(Collection objects) {
      this.objects = objects;
    }
    
    public boolean ok(Object o) {
      return objects.contains(o);
    }

}




