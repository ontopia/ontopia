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

// WARNING: This class is a direct copy of the CompactHashSet.java
// class, but with two changes. The while(...) calls has been changes
// to the new semantics and the use of hashCode() minimized. These
// changes should be repeated every time the CompactHashSet.java
// changes.

import java.util.Collection;

/**
 * INTERNAL: This class is a specialization of the CompactHashSet
 * class, and uses the == operator to compare objects.
 */
public class CompactIdentityHashSet<E> extends CompactHashSet<E> {

  public CompactIdentityHashSet() {
  }

  public CompactIdentityHashSet(int size) {
    super(size);
  }

  public CompactIdentityHashSet(Collection<E> coll) {
    super(coll);
  }

  // ===== SET IMPLEMENTATION ================================================

  /**
   * Returns <tt>true</tt> if this set contains the specified element.
   *
   * @param o element whose presence in this set is to be tested.
   * @return <tt>true</tt> if this set contains the specified element.
   */
  @Override
  public boolean contains(Object o) {
    if (o == null) {
      o = nullObject;
    }
    
    int index = (System.identityHashCode(o) & 0x7FFFFFFF) % objects.length;
    int offset = 1;

    // search for the object (continue while !null and !this object)
    while(objects[index] != null && objects[index] != o) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1) {
        offset = 2;
      }
    }

    return objects[index] != null;
  }

  /**
   * Adds the specified element to this set if it is not already
   * present.
   *
   * @param o element to be added to this set.
   * @return <tt>true</tt> if the set did not already contain the specified
   * element.
   */
  @Override
  public boolean add(Object o) {
    if (o == null) {
      o = nullObject;
    }

    int index = (System.identityHashCode(o) & 0x7FFFFFFF) % objects.length;
    int offset = 1;
    int deletedix = -1;
    
    // search for the object (continue while !null and !this object)
    while(objects[index] != null && objects[index] != o) {

      // if there's a deleted object here we can put this object here,
      // provided it's not in here somewhere else already
      if (objects[index] == deletedObject) {
        deletedix = index;
      }
      
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1) {
        offset = 2;
      }
    }
    
    if (objects[index] == null) { // wasn't present already
      if (deletedix != -1) { // reusing a deleted cell
        index = deletedix;
      } else {
        freecells--;
      }

      modCount++;
      elements++;
      objects[index] = (E)o;

      // rehash with same capacity
      if (1 - (freecells / (double) objects.length) > LOAD_FACTOR) {
        rehash();
      }
      return true;
    } else {
      // was there already
      return false;
    }
  }

  /**
   * Removes the specified element from the set.
   */
  @Override
  public boolean remove(Object o) {
    if (o == null) {
      o = nullObject;
    }
    
    int index = (System.identityHashCode(o) & 0x7FFFFFFF) % objects.length;
    int offset = 1;
    
    // search for the object (continue while !null and !this object)
    while(objects[index] != null && objects[index] != o) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1) {
        offset = 2;
      }
    }

    // we found the right position, now do the removal
    if (objects[index] != null) {
      // we found the object
      objects[index] = (E)deletedObject;
      modCount++;
      elements--;
      return true;
    } else {
      // we did not find the object
      return false;
    }
  }
  
  // ===== INTERNAL METHODS ==================================================
  
  /**
   * INTERNAL: Rehashes the hashset to a bigger size.
   */
  @Override
  protected void rehash(int newCapacity) {
    int oldCapacity = objects.length;
    E[] newObjects = (E[]) new Object[newCapacity];

    for (int ix = 0; ix < oldCapacity; ix++) {
      E o = objects[ix];
      if (o == null || o == deletedObject) {
        continue;
      }
      
      int index = (System.identityHashCode(o) & 0x7FFFFFFF) % newCapacity;
      int offset = 1;

      // search for the object
      while(newObjects[index] != null) { // no need to test for duplicates
        index = ((index + offset) & 0x7FFFFFFF) % newCapacity;
        offset = offset*2 + 1;

        if (offset == -1) {
          offset = 2;
        }
      }

      newObjects[index] = o;
    }

    objects = newObjects;
    freecells = objects.length - elements;
  }
}
