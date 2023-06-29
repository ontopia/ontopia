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
import java.util.Collection;
import java.util.Iterator;

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

  @Override
  public boolean add(E o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }
  
  @Override
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
  
  @Override
  public Iterator<E> iterator() {
    QueryResultIF result = null;
    try {
      result = (QueryResultIF)txn.executeQuery(query_iterator, params_iterator);
      return new QueryResultIterator<E>(result);
    } catch (Throwable e) {
      if (result != null) try { result.close(); } catch (Throwable t) {}
      if (e instanceof OntopiaRuntimeException) {
        throw (OntopiaRuntimeException)e;
      }
      throw new OntopiaRuntimeException(e);
    }
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
  
  @Override
  public int size() {
    QueryResultIF result = null;
    try {
      result = (QueryResultIF)txn.executeQuery(query_size, params_size);
      result.next();
      Integer integer = (Integer)result.getValue(0);
      result.close();
      return integer.intValue();
    } catch (Throwable e) {
      if (result != null) try { result.close(); } catch (Throwable t) {}
      if (e instanceof OntopiaRuntimeException) {
        throw (OntopiaRuntimeException)e;
      }
      throw new OntopiaRuntimeException(e);
    }
  }

}
