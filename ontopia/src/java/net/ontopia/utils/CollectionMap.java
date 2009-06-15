// $Id: CollectionMap.java,v 1.6 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils;

import java.util.*;

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

public class CollectionMap extends HashMap {

  protected boolean drop_empty = true;

  public CollectionMap() {
  }

  public CollectionMap(boolean drop_empty) {
    this.drop_empty = drop_empty;
  }

  // -----------------------------------------------------------------------------
  // Collection index values
  // -----------------------------------------------------------------------------

  protected Collection createCollection() {
    return new HashSet();
  }
  
  public void add(Object key, Object value) {

    // Get collection value
    Collection coll = (Collection)get(key);
    
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

  public void remove(Object key, Object value) {

    // Get collection value
    Collection coll = (Collection)get(key);
    
    // Remove from collection
    if (coll != null) {
      // Remove value
      coll.remove(value);
      // Remove key
      if (drop_empty && coll.size() == 0) remove(key);
    }

  }

  public void move(Object value, Object old_key, Object new_key) {
    remove(old_key, value);
    add(new_key, value);
  }
  
  
  // public void replace(Object key, Object old_value, Object new_value) {
  // 
  //   // Get collection value
  //   Collection coll = (Collection)get(key);
  //   
  //   // Remove from collection
  //   if (coll != null) {
  //     // Remove value
  //     coll.remove(old_value);
  //     coll.add(new_value);
  //   } else {
  //     // Create new collection
  //     coll = createCollection();
  //     coll.add(new_value);
  //     // Add new entry to index
  //     put(key, coll);      
  //   }
  // 
  // }

  // public void replaceAll(Object key, Object old_value, Object new_value) {  
  // }
  
}




