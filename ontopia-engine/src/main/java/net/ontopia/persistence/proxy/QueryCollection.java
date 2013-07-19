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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Immutable Collection implementation that delegates its
 * data retrieval to QueryIFs. This class is very useful in cases
 * where the collection is extremely large.
 */
public class QueryCollection<E> extends AbstractCollection<E> {
  protected TransactionIF txn;
  protected String query_size;
  protected String query_iterator;
  protected Object[] params_size;
  protected Object[] params_iterator;
  
  public QueryCollection(TransactionIF txn,
                         String query_size, Object[] params_size,
                         String query_iterator, Object[] params_iterator) {
    this.txn = txn;
    this.query_size = query_size;
    this.query_iterator = query_iterator;
    this.params_size = params_size;
    this.params_iterator = params_iterator;
  }

  public boolean add(E o) {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }

  // Note: Following methods are delegated to AbstractList
  
  //! public boolean contains(Object o) {
  //!   throw new UnsupportedOperationException("Not yet supported.");
  //! }
  //! 
  //! public boolean containsAll(Collection c) {
  //!   throw new UnsupportedOperationException("Not yet supported.");
  //! }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public Iterator<E> iterator() {
    QueryResultIF result = null;
    try {
      result = (QueryResultIF)txn.executeQuery(query_iterator, params_iterator);
      return new QueryResultIterator<E>(result);
    } catch (Throwable e) {
      if (result != null) try { result.close(); } catch (Throwable t) {};
      if (e instanceof OntopiaRuntimeException) throw (OntopiaRuntimeException)e;
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  
  public int size() {
    QueryResultIF result = null;
    try {
      result = (QueryResultIF)txn.executeQuery(query_size, params_size);
      result.next();
      Integer integer = (Integer)result.getValue(0);
      result.close();
      return integer.intValue();
    } catch (Throwable e) {
      if (result != null) try { result.close(); } catch (Throwable t) {};
      if (e instanceof OntopiaRuntimeException) throw (OntopiaRuntimeException)e;
      throw new OntopiaRuntimeException(e);
    }
  }

  public Object[] toArray() {
    List result = new ArrayList();
    Iterator it = iterator();
    while (it.hasNext()) {
      result.add(it.next());
    }
    return result.toArray();
  }
  
  public <T> T[] toArray(T a[]) {
    List<E> result = new ArrayList<E>();
    Iterator<E> it = iterator();
    while (it.hasNext()) {
      result.add(it.next());
    }
    return result.toArray(a);

    //! // NOTE: implementation below does not work. got too complex,
    //!    then decided to chicken out going the naive route.

    //! int orglen = a.length;
    //! Iterator it=iterator();
    //! int i=0;
    //! for (; it.hasNext(); i++) {
    //!   Object next = it.next();
    //!   if (i+1 > a.length) {
    //!     // Iterator returned more elements than the call to size
    //!     // did. This probably means that some other transaction added
    //!     // new rows and that these changes are now visible to this
    //!     // transaction. Fix by allocating new longer array.
    //!     int newCapacity = (a.length * 3)/2 + 1;
    //!     Object[] na = (Object[])java.lang.reflect.Array.newInstance(
    //!     		a.getClass().getComponentType(), newCapacity);
    //!     System.arraycopy(a, 0, na, 0, a.length);
    //!     a = na;
    //!   }
    //!   a[i] = next;
    //! }
    //! 
    //! if (i+1 > orglen && a.length > i+1) {
    //!   // chop down to actual length if exceeding input size and
    //!   // temporary allocation too big
    //!   Object[] na = (Object[])java.lang.reflect.Array.newInstance(
    //!     	      a.getClass().getComponentType(), i+1);
    //!   
    //!   System.arraycopy(a, 0, na, 0, i+1);
    //!   return na;
    //! 
    //! } else if (i+1 < a.length) {
    //!   // if input size too big set next element to null as specified
    //!   // by contract
    //!   a[i+1] = null;    
    //! }
    //! return a;
  }

}
