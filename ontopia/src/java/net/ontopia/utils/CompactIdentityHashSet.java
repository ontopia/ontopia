
// $Id: CompactIdentityHashSet.java,v 1.4 2006/04/06 07:41:24 grove Exp $

package net.ontopia.utils;

// WARNING: This class is a direct copy of the CompactHashSet.java
// class, but with two changes. The while(...) calls has been changes
// to the new semantics and the use of hashCode() minimized. These
// changes should be repeated every time the CompactHashSet.java
// changes.

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * INTERNAL: This class is a specialization of the CompactHashSet
 * class, and uses the == operator to compare objects.
 */
public class CompactIdentityHashSet extends CompactHashSet {

  // ===== SET IMPLEMENTATION ================================================

  /**
   * Returns <tt>true</tt> if this set contains the specified element.
   *
   * @param o element whose presence in this set is to be tested.
   * @return <tt>true</tt> if this set contains the specified element.
   */
  public boolean contains(Object o) {
    if (o == null) o = nullObject;
    
    int index = (System.identityHashCode(o) & 0x7FFFFFFF) % objects.length;
    int offset = 1;

    // search for the object (continue while !null and !this object)
    while(objects[index] != null && objects[index] != o) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
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
  public boolean add(Object o) {
    if (o == null) o = nullObject;

    int index = (System.identityHashCode(o) & 0x7FFFFFFF) % objects.length;
    int offset = 1;
    int deletedix = -1;
    
    // search for the object (continue while !null and !this object)
    while(objects[index] != null && objects[index] != o) {

      // if there's a deleted object here we can put this object here,
      // provided it's not in here somewhere else already
      if (objects[index] == deletedObject)
        deletedix = index;
      
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }
    
    if (objects[index] == null) { // wasn't present already
      if (deletedix != -1) // reusing a deleted cell
        index = deletedix;
      else
        freecells--;

      modCount++;
      elements++;
      objects[index] = o;

      // rehash with same capacity
      if (1 - (freecells / (double) objects.length) > LOAD_FACTOR) {
        rehash(objects.length);
        // rehash with increased capacity
        if (1 - (freecells / (double) objects.length) > LOAD_FACTOR) {
          rehash(objects.length*2 + 1);
        }
      }
      return true;
    } else // was there already 
      return false;
  }

  /**
   * Removes the specified element from the set.
   */
  public boolean remove(Object o) {
    if (o == null) o = nullObject;
    
    int index = (System.identityHashCode(o) & 0x7FFFFFFF) % objects.length;
    int offset = 1;
    
    // search for the object (continue while !null and !this object)
    while(objects[index] != null && objects[index] != o) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }

    // we found the right position, now do the removal
    if (objects[index] != null) {
      // we found the object
      objects[index] = deletedObject;
      modCount++;
      elements--;
      return true;
    } else
      // we did not find the object
      return false;
  }
  
  // ===== INTERNAL METHODS ==================================================
  
  /**
   * INTERNAL: Rehashes the hashset to a bigger size.
   */
  protected void rehash(int newCapacity) {
    int oldCapacity = objects.length;
    Object[] newObjects = new Object[newCapacity];

    for (int ix = 0; ix < oldCapacity; ix++) {
      Object o = objects[ix];
      if (o == null || o == deletedObject)
        continue;
      
      int index = (System.identityHashCode(o) & 0x7FFFFFFF) % newCapacity;
      int offset = 1;

      // search for the object
      while(newObjects[index] != null) { // no need to test for duplicates
        index = ((index + offset) & 0x7FFFFFFF) % newCapacity;
        offset = offset*2 + 1;

        if (offset == -1)
          offset = 2;
      }

      newObjects[index] = o;
    }

    objects = newObjects;
    freecells = objects.length - elements;
  }
}
