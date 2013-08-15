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

package net.ontopia.persistence.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: A set implementation that track the changes performed on
 * it. It keeps track of the objects that have been added and the ones
 * that has been removed. What makes this implementation different
 * from TrackableSet is that the field value is only loaded from the
 * storage when it is actually needed.
 */

public class TrackableLazySet extends HashSet implements TrackableCollectionIF {

  protected TransactionIF txn;
  protected IdentityIF identity;
  protected int field;
  protected boolean loaded;

  protected Set added;
  protected Set removed;

  public void dump() {
    System.out.println("(TS: " + this + ")");
    System.out.println("   (+: " + added + ")");
    System.out.println("   (-: " + removed + ")");
  }

  public TrackableLazySet(TransactionIF txn, IdentityIF identity, int field) {
    this.txn = txn;
    this.identity = identity;
    this.field = field;
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
    
    boolean result = (!loaded || super.add(o));
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

    boolean result = (!loaded || super.remove(o));
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
    loadField(); // materialize
    return new PersistentIterator(txn, true, super.iterator());
  }

  // -- other

  public boolean contains(Object o) {    
    loadField(); // materialize
    return super.contains((o instanceof PersistentIF ? ((PersistentIF)o)._p_getIdentity() : o));
  }

  public boolean containsAll(Collection c) {
    Iterator e = c.iterator();
    while (e.hasNext())
      if(!contains(e.next()))
        return false;
    
    return true;
  }

  public boolean equals(Object o) {
    loadField(); // materialize
    return super.equals(o);
  }

  public int hashCode() {
    loadField(); // materialize
    return super.hashCode();
  }

  public boolean isEmpty() {
    loadField(); // materialize
    return super.isEmpty();
  }

  public int size() {
    loadField(); // materialize
    return super.size();
  }

  public Object[] toArray() {
    // materialized in size()
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
    // materialized in size()
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
  
  // -- load field value from storage

  public boolean isLoaded() {
    return loaded;
  }

  protected void loadField() {
    if (!loaded) {
      synchronized (this) {
        if (!loaded) {
          Collection _coll = null;
          try {
            _coll = (Collection)txn.loadField(identity, field);
          } catch (IdentityNotFoundException e) {
            // let coll be null
          }
          if (_coll != null && !_coll.isEmpty()) {
            // add all loaded elements to self
            Iterator iter = _coll.iterator();
            while (iter.hasNext()) {
              super.add(iter.next());
            }
            // only remove if there's something there
            if (removed != null && !removed.isEmpty()) {
              Iterator i = removed.iterator();
              while (i.hasNext()) {
                if (!super.remove(i.next()))
                  // should not be part of removed list if not removable
                  i.remove();
              }
            }
          }
          if (added != null && !added.isEmpty()) {
            Iterator i = added.iterator();
            while (i.hasNext()) {
              if (!super.add(i.next()))
                // should not be part of added list if not addable
                i.remove();
            }
          }
          loaded = true;
        }
      }
    }
  }

}
