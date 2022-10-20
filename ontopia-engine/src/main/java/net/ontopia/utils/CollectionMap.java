/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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

  //@Override disabled for java 7 backward compatibility
  public boolean remove(Object key, Object value) {

    // Get collection value
    Collection<V> coll = get(key);

    // Remove from collection
    if (coll != null) {
      // Remove value
      boolean removed = coll.remove(value);
      // Remove key
      if (drop_empty && coll.isEmpty()) {
        remove(key);
      }
      
      return removed;
    }
    return false;
  }

  public void move(V value, K old_key, K new_key) {
    remove(old_key, value);
    add(new_key, value);
  }

}
