
package net.ontopia.utils;

import java.util.Collection;
import java.util.HashMap;

/**
 * INTERNAL: A map which stores entries containing Collection
 * values. Any object can be used as key. The add(key,value) and
 * remove(key,value) makes sure that the Collection values are updated
 * correctly.<p>
 *
 * The maintained index must only contain values implementing the
 * Collection interface. The data structure looks like this:
 * <code>{key: [value, value, ...], key: [value, ...]}</code><p>
 *
 * Empty entries are removed by default.<p>
 */

public class CollectionMap<K, V> extends HashMap<K, Collection<V>> {

  protected boolean drop_empty = true;

  public CollectionMap() {
  }

  public CollectionMap(boolean drop_empty) {
    this.drop_empty = drop_empty;
  }

  // ---------------------------------------------------------------------------
  // Collection index values
  // ---------------------------------------------------------------------------

  protected Collection<V> createCollection() {
    return new CompactHashSet<V>();
  }
  
  public void add(K key, V value) {

    // Get collection value
    Collection<V> coll = get(key);
    
    // Add to collection
    if (coll != null) {
      // Add new value
      coll.add(value);
    } else {
      // Create new collection
      coll = createCollection();
      coll.add(value);
      // Add new entry to index
      put(key, coll);
    }     
  }

  public void remove(K key, V value) {

    // Get collection value
    Collection<V> coll = get(key);
    
    // Remove from collection
    if (coll != null) {
      // Remove value
      coll.remove(value);
      // Remove key
      if (drop_empty && coll.size() == 0) remove(key);
    }

  }

  public void move(V value, K old_key, K new_key) {
    remove(old_key, value);
    add(new_key, value);
  }
  
}




