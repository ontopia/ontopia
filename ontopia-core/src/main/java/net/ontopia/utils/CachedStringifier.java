// $Id: CachedStringifier.java,v 1.10 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL: Stringifier that maintains a cache of previously
 * stringified objects. It works with any implementation of
 * StringifierIF.</p>
 * 
 * The cache is first checked to see if a stringified version already
 * exists. Otherwise the object is stringified and the cache is
 * updated.</p>
 */

public class CachedStringifier implements StringifierIF, CachedIF {

  protected StringifierIF stringifier;
  protected Map<Object, String> cache = new HashMap<Object, String>();
  
  public CachedStringifier(StringifierIF stringifier) {
    this.stringifier = stringifier;
  }

  /**
   * Gets the stringifier that is to be cached.
   */
  public StringifierIF getStringifier() {
    return stringifier;
  }
  
  /**
   * Sets the stringifier that is to be cached.
   */
  public void setStringifier(StringifierIF stringifier) {
    this.stringifier = stringifier;
  }
    
  public String toString(Object object) {
    if (object == null) return "null";
    String string = cache.get(object);
    if (string != null) return string;
    String stringified = stringifier.toString(object);
    cache.put(object, stringified);
    return stringified;
  }

  public void refresh() {
    cache.clear();
  }

}




