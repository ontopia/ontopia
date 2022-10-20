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
import java.util.NoSuchElementException;

/**
 * INTERNAL: A set implementation that wraps an identity collection
 * and presents the underlying collection as if it had PersistentIF
 * instances inside. All identity lookup is done lazily, and no state
 * is stored by this instance except for the current TransactionIF and
 * the wrapped identities collection.
 */
public class IdentityCollectionWrapper<E> implements Collection<E> {
  protected final TransactionIF txn;
  protected final Collection<?> other;

  public IdentityCollectionWrapper(TransactionIF txn, Collection<?> identities) {
    this.txn = txn;
    this.other = identities;
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

  // -- other

  @Override
  public int size() {
    return other.size();
  }

  @Override
  public boolean isEmpty() {
    return other.isEmpty();
  }

  @Override
  public boolean contains(Object o) {    
    return other.contains((o instanceof PersistentIF ? ((PersistentIF)o)._p_getIdentity() : o));
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    Iterator<?> e = c.iterator();
    while (e.hasNext()) {
      if(!contains(e.next())) {
        return false;
      }
    }
    return true;
  }

  @Override
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

  @Override
  public <T> T[] toArray(T[] a) {
    int size = size();
    if (a.length < size) {
      a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    }

    int i = 0;
    Iterator<E> it = iterator();
    for (; it.hasNext(); i++) {    
      a[i] = (T) it.next();
    }
    
    if (a.length > i+1) {
      a[i+1] = null;
    }
    
    return a;
  }
  
  // -- object

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    
    Iterator<E> i = iterator();
    boolean hasNext = i.hasNext();
    while (hasNext) {
      E o = i.next();
      buf.append(o == this ? "(this Collection)" : String.valueOf(o));
      hasNext = i.hasNext();
      if (hasNext) {
        buf.append(", ");
      }
    }
    
    buf.append(']');
    return buf.toString();
  }

  // -- iterator

  @Override
  public Iterator<E> iterator() {
    return new IdentityCollectionIterator<E>(other.iterator());
  }

  class IdentityCollectionIterator<F> implements Iterator<F> {

    private Iterator<?> iter;
    private int has_next = -1;
    private F next;

    private IdentityCollectionIterator(Iterator<?> iter) {
      this.iter = iter;
    }

    @Override
    public boolean hasNext() {
      while (has_next == -1) {
        _next(); // updates has_next
      }

      return has_next == 1;
    }

    @Override
    public F next() {
      if (has_next == 0) {
        throw new NoSuchElementException();
      } else if (has_next == 1) {
        has_next = -1;
        return next;
      } else {
        _next();
        return next();
      }
    }

    public void _next() {
      // get object from iterator
      Object o;
      try {
        o = iter.next();
      } catch (NoSuchElementException e) {
        has_next = 0;
        return;
      }
      // resolve object
      if (o == null) {
        has_next = 1;
        next = null;
      } else if (o instanceof IdentityIF) {
        try {
          o = txn.getObject((IdentityIF)o, true);
          if (o == null) {
            _next();
          } else {
            has_next = 1;
            next = (F) o;
          }
        } catch (Throwable t) {
          has_next = -1;
          next = null;
        }
      } else {
        has_next = 1;
        next = (F) o;
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

  }
  
}
