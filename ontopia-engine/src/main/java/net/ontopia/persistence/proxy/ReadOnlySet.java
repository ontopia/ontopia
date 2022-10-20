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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * INTERNAL: A set implementation that track the changes performed on
 * it. It keeps track of the objects that have been added and the ones
 * that has been removed.
 */

public class ReadOnlySet<E> implements Set<E> {

  protected TransactionIF txn;

  protected final Collection<?> coll;

  public ReadOnlySet(TransactionIF txn, Collection<?> coll) {
    this.txn = txn;
    this.coll = coll;
  }
  
  // -- immutable collection

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(E o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  // -- size

  @Override
  public int size() {
    return coll.size();
  }

  @Override
  public boolean isEmpty() {
    return coll.isEmpty();
  }

  // -- iterator

  @Override
  public Iterator<E> iterator() {
    return new PersistentIterator<E>(txn, false, coll.iterator());
  }

  // -- other

  @Override
  public boolean contains(Object o) {
    return coll.contains((o instanceof PersistentIF ? ((PersistentIF)o)._p_getIdentity() : o));
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    Iterator<?> e = c.iterator();
    while (e.hasNext()) {
      if (!contains(e.next())) {
        return false;
      }
    }
    
    return true;
  }

  @Override
  public Object[] toArray() {
    Object[] result = new Object[size()];
    Iterator<E> e = iterator();
    for (int i=0; e.hasNext(); i++) {
      result[i] = e.next();
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(T[] a) {
    int size = size();
    if (a.length < size) {
      a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    }
    
    Iterator<E> it=iterator();
    for (int i=0; i<size; i++) {
      a[i] = (T) it.next();
    }
    
    if (a.length > size) {
      a[size] = null;
    }
    
    return a;
  }
  
  // return a spliterator based on the iterator of this set, to avoid issues with the lazy-loading iterator
  // See issue 555
  @Override
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(this, Spliterator.DISTINCT & Spliterator.SIZED);
  }
}
