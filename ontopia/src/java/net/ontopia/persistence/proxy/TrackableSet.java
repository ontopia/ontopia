
// $Id: TrackableSet.java,v 1.26 2007/09/27 06:36:47 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: A set implementation that track the changes performed on
 * it. It keeps track of the objects that have been added and the ones
 * that has been removed.
 */

public class TrackableSet extends HashSet implements TrackableCollectionIF {

  protected TransactionIF txn;

  protected Set added;
  protected Set removed;

  public void dump() {
    System.out.println("(TS: " + this + ")");
    System.out.println("   (+: " + added + ")");
    System.out.println("   (-: " + removed + ")");
  }

  public TrackableSet(TransactionIF txn, Collection coll) {
    // init with coll size
    super((coll == null ? 0 : coll.size()));
    init(txn, coll);
  }
  
  private synchronized void init(TransactionIF txn, Collection coll) {
    // add to collection if not null and not empty
    if (coll != null && !coll.isEmpty()) {
      Iterator iter = coll.iterator();
      while (iter.hasNext()) {
        super.add(iter.next());
      }
    }
    this.txn = txn;
  }

  public void resetTracking() {
    // Clears the lists of added and removed objects.
    // FIXME: Figure out if clearing collection or resetting to null is faster
    added = null;
    removed = null;
    // if (added != null) added.clear();
    // if (removed != null) removed.clear();
  }
  
  public void selfAdded() {
    if (!isEmpty()) {
      if (added == null)
        added = new HashSet(this);
      else
        added.addAll(this);
    }
  }

  public Collection getAdded() {
    return added;
  }

  public Collection getRemoved() {
    return removed;
  }
  
  public boolean addWithTracking(Object _o) {
    // Make sure persistent values are represented by their identity
    Object o;
    if (_o instanceof PersistentIF) {
      o = ((PersistentIF)_o)._p_getIdentity();
      if (o == null) throw new OntopiaRuntimeException("Attempting to add PersistentIF without identity to TrackableSet");
    } else
      o = _o;
    
    boolean result = super.add(o);
    // Do not track if object wasn't really added.
    if (result) {
      // Register added object and remove object from removed objects
      if (removed == null || !removed.remove(o)) {
        // Initialize added set
        if (added == null) added = new HashSet(4);
        added.add(o);
      }
    }
    return result;
  }

  public boolean removeWithTracking(Object _o) {
    // Make sure persistent values are represented by their identity
    Object o;
    if (_o instanceof PersistentIF) {
      o = ((PersistentIF)_o)._p_getIdentity();
      if (o == null) throw new OntopiaRuntimeException("Attempting to add PersistentIF without identity to TrackableSet");
    } else
      o = _o;

    boolean result = super.remove(o);
    // Do not track if object wasn't really removed.
    if (result) {
      // Register removed object and remove object from added objects
      if (added == null || !added.remove(o)) {
        // Initialize removed set
        if (removed == null) removed = new HashSet(4);
        removed.add(o);
      }
    }
    return result;
  }

  public void clearWithTracking() {
    Iterator iter = new ArrayList(this).iterator();
    while (iter.hasNext()) {
      removeWithTracking(iter.next());
    }
  }
  
  // -- immutable collection

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public boolean add(Object o) {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(Collection c) {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  public boolean removeAll(Collection c) {
    throw new UnsupportedOperationException();
  }

  public boolean retainAll(Collection c) {
    throw new UnsupportedOperationException();
  }
  
  // -- iterator

  public Iterator iterator() {
    return new PersistentIterator(txn, true, super.iterator());
  }

  // -- other

  public boolean contains(Object o) {    
    return super.contains((o instanceof PersistentIF ? ((PersistentIF)o)._p_getIdentity() : o));
  }

  public boolean containsAll(Collection c) {
    Iterator e = c.iterator();
    while (e.hasNext())
      if(!contains(e.next()))
        return false;
    
    return true;
  }

  public Object[] toArray() {
    Object[] result = new Object[size()];
    Iterator it = iterator();

    int i = 0;
    for (; it.hasNext(); i++) {
      result[i] = it.next();
    }
    if (i+1 < result.length) {
      Object[] r = new Object[i+1];
      System.arraycopy(result, 0, r, 0, i+1);
      return r;
    } else {
      return result;
    }
  }

  public Object[] toArray(Object[] a) {
    int size = size();
    if (a.length < size)
      a = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);

    int i = 0;
    Iterator it = iterator();
    for (; it.hasNext(); i++) {    
      a[i] = it.next();
    }
    
    if (a.length > i+1)
      a[i+1] = null;
    
    return a;
  }
  
}
