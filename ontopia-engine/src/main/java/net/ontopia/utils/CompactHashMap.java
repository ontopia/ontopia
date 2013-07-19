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

import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.AbstractCollection;
import java.util.NoSuchElementException;

public class CompactHashMap<K, V> extends AbstractMap<K, V> {
  protected final static int INITIAL_SIZE = 3;
  protected final static double LOAD_FACTOR = 0.6;

  /**
   * This object is used to represent null, should clients use that as
   * a key.
   */
  protected final static Object nullObject = new Object();
  /**
   * When a key is deleted this object is put into the hashtable in
   * its place, so that other entries with the same key (collisions)
   * further down the hashtable are not lost after we delete an object
   * in the collision chain.
   */
  protected final static Object deletedObject = new Object();
  protected int elements;
  protected int freecells;
  protected K[] keys;
  protected V[] values; // object at pos x corresponds to key at pos x
  protected int modCount;

  /**
   * Constructs a new, empty set.
   */
  public CompactHashMap() {
    this(INITIAL_SIZE);
  }

  /**
   * Constructs a new, empty set.
   */
  public CompactHashMap(int size) {
    // NOTE: If array size is 0, we get a
    // "java.lang.ArithmeticException: / by zero" in add(Object).
    keys = (K[]) new Object[(size==0 ? 1 : size)];
    values = (V[]) new Object[(size==0 ? 1 : size)];
    elements = 0;
    freecells = keys.length;
    modCount = 0;
  }

  // ===== MAP IMPLEMENTATION =============================================

  /**
   * Returns the number of key/value mappings in this map.
   */
  public int size() {
    return elements;
  }
  
  /**
   * Returns <tt>true</tt> if this map contains no mappings.
   */
  public boolean isEmpty() {
    return elements == 0;
  }

  /**
   * Removes all key/value mappings in the map.
   */
  public void clear() {
    elements = 0;
    for (int ix = 0; ix < keys.length; ix++) {
      keys[ix] = null;
      values[ix] = null;
    }
    freecells = values.length;
    modCount++;
  }
  
  /**
   * Returns <tt>true</tt> if this map contains the specified key.
   */
  public boolean containsKey(Object k) {
    return keys[findKeyIndex(k)] != null;
  }
  
  /**
   * Returns <tt>true</tt> if this map contains the specified value.
   */
  public boolean containsValue(Object v) {
    if (v == null)
      v = (V)nullObject;

    for (int ix = 0; ix < values.length; ix++)
      if (values[ix] != null && values[ix].equals(v))
        return true;

    return false;
  }

  /**
   * Returns a read-only set view of the map's keys.
   */
  public Set<Entry<K, V>> entrySet() {
    throw new UnsupportedOperationException();
  }

  /**
   * Removes the mapping with key k, if there is one, and returns its
   * value, if there is one, and null if there is none.
   */
  public V remove(Object k) {
    int index = findKeyIndex(k);

    // we found the right position, now do the removal
    if (keys[index] != null) {
      // we found the object

      // same problem here as with put
      V v = values[index];
      keys[index] = (K) deletedObject;
      values[index] = (V) deletedObject;
      modCount++;
      elements--;
      return v;
    } else
      // we did not find the key
      return null;
  }

  /**
   * Adds the specified mapping to this map, returning the old value for
   * the mapping, if there was one.
   */
  public V put(K k, V v) {
    if (k == null)
      k = (K)nullObject;

    int hash = k.hashCode();
    int index = (hash & 0x7FFFFFFF) % keys.length;
    int offset = 1;
    int deletedix = -1;
    
    // search for the key (continue while !null and !this key)
    while(keys[index] != null &&
          !(keys[index].hashCode() == hash &&
            keys[index].equals(k))) {

      // if there's a deleted mapping here we can put this mapping here,
      // provided it's not in here somewhere else already
      if (keys[index] == deletedObject)
        deletedix = index;
      
      index = ((index + offset) & 0x7FFFFFFF) % keys.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }
    
    if (keys[index] == null) { // wasn't present already
      if (deletedix != -1) // reusing a deleted cell
        index = deletedix;
      else
        freecells--;

      modCount++;
      elements++;

      keys[index] = (K) k;
      values[index] = (V) v;
      
      // rehash with increased capacity
      if (1 - (freecells / (double) keys.length) > LOAD_FACTOR)
        rehash(keys.length*2 + 1);
      return null;
    } else { // was there already
      modCount++;
      V oldv = values[index];
      values[index] = (V) v;
      return oldv;
    }
  }

  /**
   * INTERNAL: Rehashes the hashmap to a bigger size.
   */
  protected void rehash(int newCapacity) {
    int oldCapacity = keys.length;
    K[] newKeys = (K[]) new Object[newCapacity];
    V[] newValues = (V[]) new Object[newCapacity];

    for (int ix = 0; ix < oldCapacity; ix++) {
      Object k = keys[ix];
      if (k == null || k == deletedObject)
        continue;
      
      int hash = k.hashCode();
      int index = (hash & 0x7FFFFFFF) % newCapacity;
      int offset = 1;

      // search for the key
      while(newKeys[index] != null) { // no need to test for duplicates
        index = ((index + offset) & 0x7FFFFFFF) % newCapacity;
        offset = offset*2 + 1;

        if (offset == -1)
          offset = 2;
      }

      newKeys[index] = (K) k;
      newValues[index] = values[ix];
    }

    keys = newKeys;
    values = newValues;
    freecells = keys.length - elements;
  }

  /**
   * Returns the value for the key k, if there is one, and null if
   * there is none.
   */
  public V get(Object k) {
    return values[findKeyIndex(k)];
  }

  /**
   * Returns a virtual read-only collection containing all the values
   * in the map.
   */
  public Collection<V> values() {
    return new ValueCollection();
  }

  /**
   * Returns a virtual read-only set of all the keys in the map.
   */
  public Set<K> keySet() {
    return new KeySet();
  }

  // --- Internal utilities

  private final int findKeyIndex(Object k) {
    if (k == null)
      k = nullObject;

    int hash = k.hashCode();
    int index = (hash & 0x7FFFFFFF) % keys.length;
    int offset = 1;

    // search for the key (continue while !null and !this key)
    while(keys[index] != null &&
          !(keys[index].hashCode() == hash &&
            keys[index].equals(k))) {
      index = ((index + offset) & 0x7FFFFFFF) % keys.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }
    return index;
  }
  
  // --- Key set

  private class KeySet<K> extends AbstractSet<K> {
    public int size() {
      return elements;
    }

    public boolean contains(Object k) {
      return containsKey(k);
    }

    public Iterator<K> iterator() {
      return new KeyIterator();
    }
  }

  private class KeyIterator<K> implements Iterator<K> {
    private int ix;
    
    private KeyIterator() {
      // walk up to first value, so that hasNext() and next() return
      // correct results
      for (; ix < keys.length; ix++)
        if (values[ix] != null && keys[ix] != deletedObject)
          break;
    }

    public boolean hasNext() {
      return ix < keys.length;
    }

    public void remove() {
      throw new UnsupportedOperationException("Collection is read-only");
    }

    public K next() {
      if (ix >= keys.length)
        throw new NoSuchElementException();
      K key = (K) keys[ix++];
      
      // walk up to next value
      for (; ix < keys.length; ix++)
        if (keys[ix] != null && keys[ix] != deletedObject)
          break;
      
      // ix now either points to next key, or outside array (if no next)
      return key;
    }
  }
  
  // --- Value collection

  private class ValueCollection<V> extends AbstractCollection<V> {
    public int size() {
      return elements;
    }

    public Iterator<V> iterator() {
      return new ValueIterator();
    }

    public boolean contains(Object v) {
      return containsValue(v);
    }
  }

  private class ValueIterator<V> implements Iterator<V> {
    private int ix;
    
    private ValueIterator() {
      // walk up to first value, so that hasNext() and next() return
      // correct results
      for (; ix < values.length; ix++)
        if (values[ix] != null && values[ix] != deletedObject)
          break;
    }

    public boolean hasNext() {
      return ix < values.length;
    }

    public void remove() {
      throw new UnsupportedOperationException("Collection is read-only");
    }

    public V next() {
      if (ix >= values.length)
        throw new NoSuchElementException();
      V value = (V) values[ix++];
      
      // walk up to next value
      for (; ix < values.length; ix++)
        if (values[ix] != null && values[ix] != deletedObject)
          break;
      
      // ix now either points to next value, or outside array (if no next)
      return value;
    }
  }
}