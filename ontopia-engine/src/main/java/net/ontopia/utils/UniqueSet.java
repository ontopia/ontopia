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
import java.util.Iterator;
import java.util.Set;

// WARNING: The internals of this class have strong dependencies of
// the internals of the CompactHashSet class. So make sure that they
// are sync.

/**
 * INTERNAL: 
 */
public class UniqueSet<E> extends CompactHashSet<E> {
  
  // Number of external references to this particular set
  private int refcount;
  //! private final static UniqueSet EMPTY_SET = new UniqueSet();
  
  private final static int OP_ADD = 1;
  private final static int OP_REMOVE = 2;

  protected int hc;
  protected int hc_modCount = -1;
  
  public UniqueSet() {
    super();
  }

  public UniqueSet(int size) {
    super(size);
  }
  
  public UniqueSet(Collection<E> c) {
    super(c.size() < INITIAL_SIZE ? INITIAL_SIZE : c.size());
    Iterator<E> e = c.iterator();
    while (e.hasNext()) {
      super.add(e.next());
    }
  }

  public UniqueSet(UniqueSet<E> s) {
    super(s.objects.length);
    // clone members
    System.arraycopy(s.objects, 0, this.objects, 0, s.objects.length);
    this.elements = s.elements;
    this.freecells = s.freecells;
  }

  public UniqueSet(UniqueSet<E> s1, UniqueSet<E> s2) {
    super((s1.objects.length > s2.objects.length ? s1.objects.length : s2.objects.length));

    UniqueSet<E> set1;
    UniqueSet<E> set2;
    if (s1.elements > s2.elements) {
      set1 = s1; set2 = s2;
    } else {
      set1 = s2; set2 = s1;
    }
    
    // clone members
    System.arraycopy(set1.objects, 0, this.objects, 0, set1.objects.length);
    this.elements = set1.elements;
    this.freecells = set1.freecells;

    // add other sets elements individually
    Iterator<E> iter = set2.iterator();
    for (int i=0; i < set2.elements; i++) {
      super.add(iter.next());
    }
  }
  
  private UniqueSet(UniqueSet<E> s, int op, E o) {
    this(s);
    if (op == OP_ADD) {
      // add object
      super.add(o);
    } else if (op == OP_REMOVE) {
      // remove object
      super.remove(o);
    } else {
      throw new IllegalArgumentException("Unknown op argument: " + op);
    }
  }

  public int getReferenceCount() {
    return refcount;
  }
  
  /**
   * INTERNAL: Get the internal representation of a given set. The
   * initial reference count is 1.
   */
  public UniqueSet<E> get(Set<E> set) {
    UniqueSet<E> set2;

    //! if (set.isEmpty()) {
    //!   // Replace with EMPTY_SET if empty.
    //!   set2 = EMPTY_SET;
    //!   if (set2.refcount == 0) super.add(EMPTY_SET);
    //! } else {
      // Look up candidate set in set pool
      set2 = (UniqueSet<E>)lookup(set);
      if (set2 == null) {
        // Create new set if no existing set found
        // FIXME: Could use 'set' variable if compatible and manageable
        set2 = new UniqueSet<E>(set);
        super.add(set2);
      }
    //!}
    set2.refcount++;
    return set2;
  }

  /**
   * INTERNAL: Looks up the object in the hashset and returns the
   * actual object stored at the hashset. Note that this might be
   * another object, but one that is considered to be equal.
   */
  protected E lookup(Object o) {
    if (o == null) {
      o = nullObject;
    }
    
    int hash = o.hashCode();
    int index = (hash & 0x7FFFFFFF) % objects.length;
    int offset = 1;

    // search for the object (continue while !null and !this object)
    while(objects[index] != null &&
          !(objects[index].hashCode() == hash &&
            objects[index].equals(o))) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1) {
        offset = 2;
      }
    }

    return objects[index];
  }
  
  public void dereference(UniqueSet<E> set) {
    //! if (set == EMPTY_SET) {
    //!   if (set.refcount < 1)
    //!     throw new IllegalArgumentException("Set " + set + " is not registered with this pool.");
    //!   set.refcount--;
    //! } else {
    //!   if (!contains(set))
    //!     throw new IllegalArgumentException("Set " + set + " is not registered with this pool.");
    //!   // decrement reference count
    //!   set.refcount--;
    //! }
    if (!contains((E)set)) {
      throw new IllegalArgumentException("Set " + set + " is not registered with this pool.");
    }
    set.refcount--;
    if (set.refcount == 0) {
      super.remove((E)set);
    }
    //! if (set.refcount == 0) System.out.println("DEREF: " + set);
  }
  
  protected boolean equalsAdd(UniqueSet<E> other, Object o) {
    if (other.elements+1 != elements || !contains((E)o)) {
      return false;
    }
    for (int i=0; i < other.objects.length; i++) {
      if (other.objects[i] == null || other.objects[i] == deletedObject) {
        continue;
      }
      if (!contains((E)other.objects[i])) {
        return false;
      }
    }
    return true;
  }

  protected boolean equalsRemove(UniqueSet<E> other, Object o) {
    if (other.elements-1 != elements || contains((E)o)) {
      return false;
    }
    for (int i=0; i < other.objects.length; i++) {
      if (other.objects[i] == null || other.objects[i] == deletedObject) {
        continue;
      }
      if (!contains((E)other.objects[i]) && !other.objects[i].equals(o)) {
        return false;
      }
    }
    return true;
  }
  
  public UniqueSet<E> add(UniqueSet<E> set, Object o, boolean dereference) {
    if (o == null) {
      o = nullObject;
    }
    if (set.contains((E)o)) {
      return set;
    }
    
    // decrement reference count
    if (dereference) {
      dereference(set);
    }
    
    int hash = set.hashCode() + o.hashCode();
    int index = (hash & 0x7FFFFFFF) % objects.length;
    int offset = 1;

    // search for the added set
    while(objects[index] != null && 
          !(objects[index].hashCode() == hash &&
            ((UniqueSet)objects[index]).equalsAdd(set, o))) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1) {
        offset = 2;
      }
    }

    if (objects[index] == null || objects[index] == deletedObject) { // wasn't present already
      UniqueSet newset = new UniqueSet(set, OP_ADD, o);
      // reference count = 1
      newset.refcount = 1;
      //! System.out.println("ADDED: " + newset.refcount + " " + newset);
      super.add(newset);
      return newset;
    } else { // was there already
      // increment reference count
      UniqueSet oldset = (UniqueSet)objects[index];
      oldset.refcount++;    
      //! System.out.println("REUSE: " + oldset.refcount + " " + oldset);
      return oldset;
    }
  }

  @SuppressWarnings("unchecked")
  public UniqueSet<E> remove(UniqueSet<E> set, Object o, boolean dereference) {
    if (o == null) {
      o = nullObject;
    }
    if (!set.contains((E)o)) {
      return set;
    }
    
    // decrement reference count
    if (dereference) {
      dereference(set);
    }
    
    int hash = set.hashCode() - o.hashCode();
    int index = (hash & 0x7FFFFFFF) % objects.length;
    int offset = 1;

    // search for the removed set
    while(objects[index] != null &&
          !(objects[index].hashCode() == hash &&
            ((UniqueSet<E>)objects[index]).equalsRemove(set, o))) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1) {
        offset = 2;
      }
    }

    if (objects[index] == null || objects[index] == deletedObject) { // wasn't present already
      UniqueSet<E> newset = new UniqueSet<E>(set, OP_REMOVE, (E)o);
      // reference count = 1
      newset.refcount = 1;
      //! System.out.println("ADDED: " + newset.refcount + " " + newset);
      super.add(newset);
      return newset;
    } else { // was there already
      // increment reference count
      UniqueSet<E> oldset = (UniqueSet<E>)objects[index];
      oldset.refcount++;
      //! System.out.println("REUSE: " + oldset.refcount + " " + oldset);
      return oldset;
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void dump() {
    System.out.println("Reference count: " + refcount);
    Iterator<E> iter = iterator();
    for (int i=0; i < elements; i++) {
      UniqueSet<E> set = (UniqueSet<E>)iter.next();
      System.out.println("=> " + set.refcount + ": " + set);
    }
  }
  
  // ---- Set is immutable
  
  @Override
	public boolean add(Object o) {
    throw new UnsupportedOperationException();
  }  
  @Override
	public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }
  @Override
	public boolean addAll(Collection<? extends E> coll) {
    throw new UnsupportedOperationException();
  }
  @Override
	public boolean removeAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  @Override
	public boolean retainAll(Collection<?> coll) {
    throw new UnsupportedOperationException();
  }
  @Override
	public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return super.toString() + getReferenceCount();
  }
  
  @Override
  public int hashCode() {
    // recompute hashcode only when neccessary
    if (modCount != hc_modCount) {
      hc_modCount = modCount;
      hc = super.hashCode();      
    }
    return hc;
  }
  
}
