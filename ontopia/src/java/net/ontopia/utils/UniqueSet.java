
// $Id: UniqueSet.java,v 1.9 2005/09/20 12:00:21 grove Exp $

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
public class UniqueSet extends CompactHashSet {
  
  public UniqueSet() {
    super();
  }

  public UniqueSet(int size) {
    super(size);
  }
  
  public UniqueSet(Collection c) {
    super(c.size() < INITIAL_SIZE ? INITIAL_SIZE : c.size());
    Iterator e = c.iterator();
    while (e.hasNext()) {
      super.add(e.next());
    }
  }

  // Number of external references to this particular set
  private int refcount;
  //! private final static UniqueSet EMPTY_SET = new UniqueSet();
  
  private final static int OP_ADD = 1;
  private final static int OP_REMOVE = 2;

  public UniqueSet(UniqueSet s) {
    super(s.objects.length);
    // clone members
    System.arraycopy(s.objects, 0, this.objects, 0, s.objects.length);
    this.elements = s.elements;
    this.freecells = s.freecells;
  }

  public UniqueSet(UniqueSet s1, UniqueSet s2) {
    super((s1.objects.length > s2.objects.length ? s1.objects.length : s2.objects.length));

    UniqueSet set1;
    UniqueSet set2;
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
    Iterator iter = set2.iterator();
    for (int i=0; i < set2.elements; i++) {
      super.add(iter.next());
    }
  }
  
  private UniqueSet(UniqueSet s, int op, Object o) {
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
  public UniqueSet get(Set set) {
    UniqueSet set2;

    //! if (set.isEmpty()) {
    //!   // Replace with EMPTY_SET if empty.
    //!   set2 = EMPTY_SET;
    //!   if (set2.refcount == 0) super.add(EMPTY_SET);
    //! } else {
      // Look up candidate set in set pool
      set2 = (UniqueSet)lookup(set);
      if (set2 == null) {
        // Create new set if no existing set found
        // FIXME: Could use 'set' variable if compatible and manageable
        set2 = new UniqueSet(set);
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
  protected Object lookup(Object o) {
    if (o == null) o = nullObject;
    
    int hash = o.hashCode();
    int index = (hash & 0x7FFFFFFF) % objects.length;
    int offset = 1;

    // search for the object (continue while !null and !this object)
    while(objects[index] != null &&
          !(objects[index].hashCode() == hash &&
            objects[index].equals(o))) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }

    return objects[index];
  }
  
  public void dereference(UniqueSet set) {
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
    if (!contains(set))
      throw new IllegalArgumentException("Set " + set + " is not registered with this pool.");
    set.refcount--;
    if (set.refcount == 0) super.remove(set);
    //! if (set.refcount == 0) System.out.println("DEREF: " + set);
  }
  
  protected boolean equalsAdd(UniqueSet other, Object o) {
    if (other.elements+1 != elements || !contains(o)) return false;    
    for (int i=0; i < other.objects.length; i++) {
      if (other.objects[i] == null || other.objects[i] == deletedObject) continue;
      if (!contains(other.objects[i])) return false;
    }
    return true;
  }

  protected boolean equalsRemove(UniqueSet other, Object o) {
    if (other.elements-1 != elements || contains(o)) return false;    
    for (int i=0; i < other.objects.length; i++) {
      if (other.objects[i] == null || other.objects[i] == deletedObject) continue;
      if (!contains(other.objects[i]) && !other.objects[i].equals(o)) return false;
    }
    return true;
  }
  
  public UniqueSet add(UniqueSet set, Object o, boolean dereference) {
    if (o == null) o = nullObject;
    if (set.contains(o)) return set;
    
    // decrement reference count
    if (dereference) dereference(set);
    
    int hash = set.hashCode() + o.hashCode();
    int index = (hash & 0x7FFFFFFF) % objects.length;
    int offset = 1;

    // search for the added set
    while(objects[index] != null && 
          !(objects[index].hashCode() == hash &&
            ((UniqueSet)objects[index]).equalsAdd(set, o))) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
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

  public UniqueSet remove(UniqueSet set, Object o, boolean dereference) {
    if (o == null) o = nullObject;
    if (!set.contains(o)) return set;
    
    // decrement reference count
    if (dereference) dereference(set);
    
    int hash = set.hashCode() - o.hashCode();
    int index = (hash & 0x7FFFFFFF) % objects.length;
    int offset = 1;

    // search for the removed set
    while(objects[index] != null &&
          !(objects[index].hashCode() == hash &&
            ((UniqueSet)objects[index]).equalsRemove(set, o))) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }

    if (objects[index] == null || objects[index] == deletedObject) { // wasn't present already
      UniqueSet newset = new UniqueSet(set, OP_REMOVE, o);
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
  
  public void dump() {
    System.out.println("Reference count: " + refcount);
    Iterator iter = iterator();
    for (int i=0; i < elements; i++) {
      UniqueSet set = (UniqueSet)iter.next();
      System.out.println("=> " + set.refcount + ": " + set);
    }
  }
  
  // ---- Set is immutable
  
	public boolean add(Object o) {
    throw new UnsupportedOperationException();
  }  
	public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }
	public boolean addAll(Collection coll) {
    throw new UnsupportedOperationException();
  }
	public boolean removeAll(Collection coll) {
    throw new UnsupportedOperationException();
  }
	public boolean retainAll(Collection coll) {
    throw new UnsupportedOperationException();
  }
	public void clear() {
    throw new UnsupportedOperationException();
  }

  public String toString() {
    return super.toString() + getReferenceCount();
  }
  
  protected int hc;
  protected int hc_modCount = -1;
  
  public int hashCode() {
    // recompute hashcode only when neccessary
    if (modCount != hc_modCount) {
      hc_modCount = modCount;
      hc = super.hashCode();      
    }
    return hc;
  }
  
}
