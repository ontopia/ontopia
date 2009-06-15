
// $Id: LookupIndexIF.java,v 1.7 2007/09/10 07:16:28 geir.gronmo Exp $

package net.ontopia.utils;

/**
 * INTERNAL: An interface implemented by objects which can be used to
 * look up information, but which can do no more. A simplified version
 * of the Map interface, used for lookup.
 */

public interface LookupIndexIF {

  public Object get(Object key);

  public Object put(Object key, Object value);

  public Object remove(Object key);
  
}
