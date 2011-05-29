
package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: A non-synchronized lookup index adapter for Map instances.
 */

public class MapIndex implements LookupIndexIF, ClearableIF {

  protected final Map map;
  
  public MapIndex(Map map) {
    this.map = map;
  }
  
  public Object get(Object key) {
    return map.get(key);
  }

  public Object put(Object key, Object value) {
    return map.put(key, value);
  }

  public Object remove(Object key) {
    return map.remove(key);
  }

  // --- ClearableIF
  
  public void clear() {
    map.clear();
  }
  
}
