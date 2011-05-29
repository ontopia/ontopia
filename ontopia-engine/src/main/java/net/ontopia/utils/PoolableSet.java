
package net.ontopia.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

// WARNING: The internals of this class have strong dependencies of
// the internals of the CompactHashSet class. So make sure that they
// are sync.

/**
 * INTERNAL: The default PoolableSet implementation. This is class is
 * used by the default SetPool implementation.
 */

public class PoolableSet extends CompactHashSet implements PoolableSetIF {
  
  public PoolableSet() {
    super();
  }

  public PoolableSet(int size) {
    super(size);
  }
  
  public PoolableSet(Collection c) {
    super(c.size() < INITIAL_SIZE ? INITIAL_SIZE : c.size());
    Iterator e = c.iterator();
    while (e.hasNext()) {
	    super.add(e.next());
    }
  }

  public PoolableSet(CompactHashSet s) {
    super(s.objects.length);
    // clone members
    System.arraycopy(s.objects, 0, this.objects, 0, s.objects.length);
    this.elements = s.elements;
    this.freecells = s.freecells;
  }

  //! public PoolableSet(CompactHashSet s1, CompactHashSet s2) {
  //!   super((s1.objects.length > s2.objects.length ? s1.objects.length : s2.objects.length));
  //! 
  //!   CompactHashSet set1;
  //!   CompactHashSet set2;
  //!   if (s1.elements > s2.elements) {
  //!     set1 = s1; set2 = s2;
  //!   } else {
  //!     set1 = s2; set2 = s1;
  //!   }
  //!   
  //!   // clone members
  //!   System.arraycopy(set1.objects, 0, this.objects, 0, set1.objects.length);
  //!   this.elements = set1.elements;
  //!   this.freecells = set1.freecells;
  //! 
  //!   // add other sets elements individually
  //!   Iterator iter = set2.iterator();
  //!   for (int i=0; i < set2.elements; i++) {
  //!     super.add(iter.next());
  //!   }
  //! }

  // NOTE: Methods to circumvent the set immutability of this
  // class. The methods should only be done in a controlled
  // environement.
  
  public boolean _add(Object o) {
    return super.add(o);
  }

  public boolean _remove(Object o) {
    return super.remove(o);
  }
  
  // -----------------------------------------------------------------------------
  // PoolableSetIF implementation
  // -----------------------------------------------------------------------------

  // Number of external references to this particular set
  protected int refcount;

  public int getReferenceCount() {
    return refcount;
  }

  public int referenced(SetPoolIF pool) {
    // +1 and return reference count
    return ++refcount;
  }
  
  public int dereferenced(SetPoolIF pool) {
    // -1 and return reference count
    return --refcount;
  }
  
  public boolean equalsAdd(Set other, Object o) {
    if (other instanceof CompactHashSet) {
      // Set is CompactHashSet
      CompactHashSet chsset = (CompactHashSet)other;
      if (chsset.elements+1 != elements || !contains(o)) return false;    
      for (int i=0; i < chsset.objects.length; i++) {
        if (chsset.objects[i] == null || chsset.objects[i] == deletedObject) continue;
        if (!contains(chsset.objects[i])) return false;
      }
      return true;
    } else {
      // Set is generic set
      int size = other.size();
      if (size+1 != elements || !contains(o)) return false;
      Iterator iter = other.iterator();
      for (int i=0; i < size; i++) {
        Object x = iter.next();
        if (x == null || x == deletedObject) continue;
        if (!contains(x)) return false;
      }
      return true;
    }
  }

  public boolean equalsRemove(Set other, Object o) {
    if (other instanceof CompactHashSet) {
      // Set is CompactHashSet
      CompactHashSet chsset = (CompactHashSet)other;
      if (chsset.elements-1 != elements || contains(o)) return false;    
      for (int i=0; i < chsset.objects.length; i++) {
        if (chsset.objects[i] == null || chsset.objects[i] == deletedObject) continue;
        if (!contains(chsset.objects[i]) && !chsset.objects[i].equals(o)) return false;
      }
      return true;
    } else {
      // Set is generic set
      int size = other.size();
      if (size-1 != elements || contains(o)) return false;
      Iterator iter = other.iterator();
      for (int i=0; i < size; i++) {
        Object x = iter.next();
        if (x == null || x == deletedObject) continue;
        if (!contains(x) && !x.equals(o)) return false;
      }
      return true;
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
