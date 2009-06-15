
// $Id: EqualsFilter.java,v 1.3 2008/05/29 10:54:59 geir.gronmo Exp $

package net.ontopia.topicmaps.query.spi;

import net.ontopia.utils.ObjectUtils;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * EXPERIMENTAL: Sample filter implementation that returns true if all
 * arguments are equal.<p>
 *
 * @since 4.0
 */

public class EqualsFilter extends FilterPredicate {

  public boolean filter(Object[] objects) throws InvalidQueryException {
    // return true if  all objects are equal.
    if (objects.length > 1) {
      for (int i=1; i < objects.length; i++) {
        if (ObjectUtils.different(objects[i-1], objects[i]))
          return false;
      }      
    }
    return true;
  }

}
