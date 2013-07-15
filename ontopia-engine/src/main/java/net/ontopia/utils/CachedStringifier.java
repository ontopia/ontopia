
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

public class CachedStringifier<T> implements StringifierIF<T>, CachedIF {

  protected StringifierIF<? super T> stringifier;
  protected Map<T, String> cache = new HashMap<T, String>();
  
  public CachedStringifier(StringifierIF<? super T> stringifier) {
    this.stringifier = stringifier;
  }

  /**
   * Gets the stringifier that is to be cached.
   */
  public StringifierIF<? super T> getStringifier() {
    return stringifier;
  }
  
  /**
   * Sets the stringifier that is to be cached.
   */
  public void setStringifier(StringifierIF<? super T> stringifier) {
    this.stringifier = stringifier;
  }
    
  public String toString(T object) {
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




