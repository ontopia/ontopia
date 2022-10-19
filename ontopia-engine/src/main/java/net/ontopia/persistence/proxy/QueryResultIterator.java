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

import java.util.Iterator;
import java.util.NoSuchElementException;
  
/**
 * INTERNAL: Interator wrapper class for QueryResultIFs. A column
 * index can be specified to iterate certain columns. The default
 * column is 0. The iterator does not currently support the remove()
 * method.
 */
public class QueryResultIterator<E> implements Iterator<E> {
  protected QueryResultIF result;
  protected int index;

  protected boolean has_next;
  
  public QueryResultIterator(QueryResultIF result) {
    this(result, 0);
  }
  
  public QueryResultIterator(QueryResultIF result, int index) {
    this.result = result;
    this.index = index;

    // check to see if there is a next element
    has_next = result.next();
    if (!has_next) { close(); }
  }
  
  @Override
  public synchronized boolean hasNext() {
    return has_next;
  }
  
  @Override
  public E next() {
    if (!has_next)
      throw new NoSuchElementException();
    synchronized (this) {
      // return value at given index
      E value = (E) result.getValue(index);
      // skip to next row
      has_next = result.next();
      if (!has_next) { close(); }
      return value;
    }
  }
  
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  /**
   * INTERNAL: Close iterator and release any resources held by it.
   */
  public void close() {
    // close query result
    if (result != null) {
      synchronized (this) {
	try {
	  result.close();
	} finally {
	  result = null;
	  has_next = false;
	}
      }
    }
  }
  
  @Override
  protected void finalize() {
    close();
  }
  
}
