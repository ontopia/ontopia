
package net.ontopia.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

// WARNING: The internals of this class have strong dependencies of
// the internals of the CompactHashSet class. So make sure that they
// are sync.

/**
 * INTERNAL: The default SetPool implementation.
 *
 * @since 2.0
 */
public class SetPool extends CompactHashSet implements SetPoolIF, PoolableSetFactoryIF {

  private PoolableSetIF EMPTY_SET;
  protected PoolableSetFactoryIF factory;
  
  public SetPool() {
    this((PoolableSetFactoryIF)null);
  }
  
  public SetPool(PoolableSetFactoryIF factory) {
    super();
    this.factory = (factory == null ? this : factory);
    this.EMPTY_SET = this.factory.createSet();
  }

  public SetPool(int size) {
    this(size, null);
  }
  
  public SetPool(int size, PoolableSetFactoryIF factory) {
    super(size);
    this.factory = (factory == null ? this : factory);
    this.EMPTY_SET = this.factory.createSet();
  }
  
  public SetPool(Collection c) {
    this(c, null);
  }
  
  public SetPool(Collection c, PoolableSetFactoryIF factory) {
    super(c.size() < INITIAL_SIZE ? INITIAL_SIZE : c.size());
    this.factory = (factory == null ? this : factory);
    this.EMPTY_SET = this.factory.createSet();
    Iterator e = c.iterator();
    while (e.hasNext()) {
	    super.add(e.next());
    }
  }
  
  // -- PoolableSetFactoryIF implementation
  
  // NOTE: The create* methods can be used by subclasses to create
  // their own poolable sets.
  
  public PoolableSetIF createSet() {
    return new PoolableSet();
  }

  public PoolableSetIF createSet(Set set) {
    return new PoolableSet(set);
  }
  
  public PoolableSetIF createSetAdd(Set set, Object o) {
    PoolableSet pset =  new PoolableSet((CompactHashSet)set);
    pset._add(o);
    return pset;
  }
  
  public PoolableSetIF createSetRemove(Set set, Object o) {
    PoolableSet pset = new PoolableSet((CompactHashSet)set);
    pset._remove(o);
    return pset;
  }

  // -- Methods called when set is being registered or unregistered
  // with the setpool.
  
  protected void registerSet(Set set) {
    super.add(set);
  }
  
  protected void unregisterSet(Set set) {
    super.remove(set);
  }
  
  /**
   * INTERNAL: Get the internal representation of a given set. The
   * initial reference count is 1.
   */
  public Set reference(Set set) {
    PoolableSetIF poolable;
    if (set.isEmpty()) {
      // If set is empty use prepared empty set
      poolable = EMPTY_SET;
      if (poolable.getReferenceCount() == 0)
        registerSet(EMPTY_SET);
    } else {
      // Look up candidate set in set pool
      poolable = (PoolableSetIF)lookup(set);
      if (poolable == null) {
        // Create new set if no existing set found
        // FIXME: Could use 'set' variable if compatible and manageable
        poolable = factory.createSet(set);
        registerSet(poolable);
      }
    }
    // Increment reference count
    poolable.referenced(this);
    return poolable;
  }
  
  public void dereference(Set set) {
    PoolableSetIF poolable = (PoolableSetIF)set;
    if (poolable == EMPTY_SET) {
      if (poolable.getReferenceCount() < 1)
        throw new IllegalArgumentException("Set " + set + " is not registered with this pool.");
    }
    else if (!contains(poolable)) {
      throw new IllegalArgumentException("Set " + set + " is not registered with this pool.");
    }
    // decrement reference count
    if (poolable.dereferenced(this) == 0) unregisterSet(set);
    //! if (refcount == 0) System.out.println("DEREF: " + poolable);
  }

  /**
   * INTERNAL: Looks up the object in the hashset and returns the
   * actual object stored at the hashset. Note that this might be
   * another object, but one that is considered to be equal. Returns
   * null if the object was not found.
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
  
  public Set add(Set set, Object o, boolean dereference) {
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
            ((PoolableSetIF)objects[index]).equalsAdd(set, o))) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }

    if (objects[index] == null || objects[index] == deletedObject) { // wasn't present already
      PoolableSetIF newset = factory.createSetAdd(set, o);
      // ASSERT: reference count = 1
      newset.referenced(this); // FIXME: newset.setReferenceCount(1);
      //! System.out.println("ADDED: " + newset.getReferenceCount() + " " + newset);
      registerSet(newset);
      return newset;
    } else { // was there already
      // increment reference count
      PoolableSetIF oldset = (PoolableSetIF)objects[index];
      oldset.referenced(this);
      //! System.out.println("REUSE: " + oldset.getReferenceCount() + " " + oldset);
      return oldset;
    }
  }

  public Set remove(Set set, Object o, boolean dereference) {
    if (o == null) o = nullObject;
    if (!set.contains(o)) return set;
    
    // decrement reference count    
    if (dereference) dereference(set);

    // return empty set if removed set will be empty
    if (set.size()-1 == 0) {
      if (EMPTY_SET.getReferenceCount() == 0)
        registerSet(EMPTY_SET);
      EMPTY_SET.referenced(this);
      return EMPTY_SET;
    }
    
    int hash = set.hashCode() - o.hashCode();
    int index = (hash & 0x7FFFFFFF) % objects.length;
    int offset = 1;

    // search for the removed set
    while(objects[index] != null &&
          !(objects[index].hashCode() == hash &&
            ((PoolableSetIF)objects[index]).equalsRemove(set, o))) {
      index = ((index + offset) & 0x7FFFFFFF) % objects.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }

    if (objects[index] == null || objects[index] == deletedObject) { // wasn't present already
      PoolableSetIF newset = factory.createSetRemove(set, o);
      // ASSERT: reference count = 1
      newset.referenced(this); // FIXME: newset.setReferenceCount(1);
      //! System.out.println("ADDED: " + newset.getReferenceCount() + " " + newset);
      registerSet(newset);
      return newset;
    } else { // was there already
      // increment reference count
      PoolableSetIF oldset = (PoolableSetIF)objects[index];
      oldset.referenced(this);
      //! System.out.println("REUSE: " + oldset.getReferenceCount() + " " + oldset);
      return oldset;
    }
  }
  
  public void dump() {
    Iterator iter = iterator();
    for (int i=0; i < elements; i++) {
      PoolableSetIF set = (PoolableSetIF)iter.next();
      System.out.println("=> " + set.getReferenceCount() + ": " + set);
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
