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

/**
 * INTERNAL: A set implementation that track the changes performed on
 * it. It keeps track of the objects that have been added and the ones
 * that has been removed.
 */

public class ReadOnlySet implements Set {

  protected TransactionIF txn;

  protected Collection coll;

  public ReadOnlySet(TransactionIF txn, Collection coll) {
    this.txn = txn;
    this.coll = coll;
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

  // -- size

  public int size() {
    return coll.size();
  }

  public boolean isEmpty() {
    return coll.isEmpty();
  }

  // -- iterator

  public Iterator iterator() {
    return new PersistentIterator(txn, false, coll.iterator());
  }

  // -- other

  public boolean contains(Object o) {
    return coll.contains((o instanceof PersistentIF ? ((PersistentIF)o)._p_getIdentity() : o));
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
    Iterator e = iterator();
    for (int i=0; e.hasNext(); i++)
      result[i] = e.next();
    return result;
  }

  public Object[] toArray(Object[] a) {
    int size = size();
    if (a.length < size)
      a = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    
    Iterator it=iterator();
    for (int i=0; i<size; i++)
      a[i] = it.next();
    
    if (a.length > size)
      a[size] = null;
    
    return a;
  }
  
}
