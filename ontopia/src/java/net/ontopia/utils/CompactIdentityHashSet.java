
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
public class CompactIdentityHashSet extends java.util.AbstractSet {
  
  protected final static int INITIAL_SIZE = 3;
  protected final static double LOAD_FACTOR = 0.75;
  protected final static Object nullObject = new Object();
  protected final static Object deletedObject = new Object();
  protected int elements;
  protected int freecells;
  protected Object[] objects;
  protected int modCount;
  
  /**
   * Constructs a new, empty set.
   */
  public CompactIdentityHashSet() {
    objects = new Object[INITIAL_SIZE];
    elements = 0;
    freecells = objects.length;
    modCount = 0;
  }

  /**
   * Constructs a new, empty set.
   */
  public CompactIdentityHashSet(int size) {
    // NOTE: If array size is 0, we get a
    // "java.lang.ArithmeticException: / by zero" in add(Object).
    objects = new Object[(size==0 ? 1 : size)]; 
    elements = 0;
    freecells = objects.length;
    modCount = 0;
  }

  /**
   * Constructs a new set containing the elements in the specified
   * collection.
   *
   * @param c the collection whose elements are to be placed into this set.
   */
  public CompactIdentityHashSet(Collection c) {
    this(c.size());
    addAll(c);
  }

  // ===== SET IMPLEMENTATION ================================================
  
  /**
   * Returns an iterator over the elements in this set.  The elements
   * are returned in no particular order.
   *
   * @return an Iterator over the elements in this set.
   * @see ConcurrentModificationException
   */
  public Iterator iterator() {
    return new CompactHashIterator();
  }

  /**
   * Returns the number of elements in this set (its cardinality).
   */
  public int size() {
    return elements;
  }

  /**
   * Returns <tt>true</tt> if this set contains no elements.
   */
  public boolean isEmpty() {
    return elements == 0;
  }

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
  
  /**
   * Removes all of the elements from this set.
   */
  public void clear() {
    elements = 0;
    for (int ix = 0; ix < objects.length; ix++)
      objects[ix] = null;
    freecells = objects.length;
    modCount++;
  }

  public Object[] toArray() {
    Object[] result = new Object[elements];
    Object[] objects = this.objects;
    int pos = 0;
    for (int i = 0; i < objects.length; i++)
      if (objects[i] != null && objects[i] != deletedObject) {
        if (objects[i] == nullObject)
          result[pos++] = null;
        else
          result[pos++] = objects[i];
      }
    return result;
  }

  public Object[] toArray(Object a[]) {
    int size = elements;
    if (a.length < size)
      a = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    Object[] objects = this.objects;
    int pos = 0;
    for (int i = 0; i < objects.length; i++)
      if (objects[i] != null && objects[i] != deletedObject) {
        if (objects[i] == nullObject)
          a[pos++] = null;
        else
          a[pos++] = objects[i];
      }
    return a;
  }  
  
  // ===== INTERNAL METHODS ==================================================

  /**
   * INTERNAL: Used for debugging only.
   */
  public void dump() {
    System.out.println("Size: " + objects.length);
    System.out.println("Elements: " + elements);
    System.out.println("Free cells: " + freecells);
    System.out.println();
    for (int ix = 0; ix < objects.length; ix++) 
      System.out.println("[" + ix + "]: " + objects[ix]);
  }
  
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
  
  // ===== ITERATOR IMPLEMENTATON ============================================
  
  private class CompactHashIterator implements Iterator {
    private int index;
    private int lastReturned = -1;
    
    /**
     * The modCount value that the iterator believes that the backing
     * CompactIdentityHashSet should have.  If this expectation is violated,
     * the iterator has detected concurrent modification.
     */
    private int expectedModCount;

    public CompactHashIterator() {
      for (index = 0; index < objects.length &&
             (objects[index] == null ||
              objects[index] == deletedObject); index++)
        ;
      expectedModCount = modCount;
    }

    public boolean hasNext() {
      return index < objects.length;
    }

    public Object next() {
      if (modCount != expectedModCount)
        throw new ConcurrentModificationException();
      if (index >= objects.length) {
        lastReturned = -2;
        throw new NoSuchElementException();
      }

      lastReturned = index;
      for (index += 1; index < objects.length &&
             (objects[index] == null ||
              objects[index] == deletedObject); index++)
        ;
      if (objects[lastReturned] == nullObject)
        return null;
      else
        return objects[lastReturned];
    }

    public void remove() {
      if (modCount != expectedModCount)
        throw new ConcurrentModificationException();
      if (lastReturned == -1 || lastReturned == -2)
        throw new IllegalStateException();
      // delete object
      if (objects[lastReturned] != null && objects[lastReturned] != deletedObject) {
        objects[lastReturned] = deletedObject;
        elements--;
      }
    }
  }
  
}
