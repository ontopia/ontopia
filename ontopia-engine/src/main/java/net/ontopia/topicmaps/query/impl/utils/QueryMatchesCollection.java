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

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: A set implementation that wraps an QueryMatches instance
 * and presents the values in one of the columns as a collection.
*/

public class QueryMatchesCollection implements Collection {

  protected QueryMatches matches;
  protected int colidx;

  public QueryMatchesCollection(QueryMatches matches, int colidx) {
    this.matches = matches;
    this.colidx = colidx;
  }
  
  // -- immutable collection

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection c) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection c) {
    throw new UnsupportedOperationException();
  }

  // -- other

  @Override
  public int size() {
    return matches.last+1;
  }

  @Override
  public boolean isEmpty() {
    return matches.isEmpty();
  }

  @Override
  public boolean contains(Object o) {    
    // linear scan
    if (o == null) {
      for (int row = 0; row <= matches.last; row++) {
	if (matches.data[row][this.colidx] == null) {
    return true;
  }
      }
      return false;
    } else {
      for (int row = 0; row <= matches.last; row++) {
	if (o.equals(matches.data[row][this.colidx])) {
    return true;
  }
      }
      return false;
    }
  }

  @Override
  public boolean containsAll(Collection c) {
    Iterator e = c.iterator();
    while (e.hasNext()) {
      if (!contains(e.next())) {
        return false;
      }
    }
    
    return true;
  }

  @Override
  public Object[] toArray() {
    Object[] result = new Object[matches.last];
    for (int row = 0; row <= matches.last; row++) {
      result[row] = matches.data[row][colidx];
    }
    return result;
  }

  @Override
  public Object[] toArray(Object[] a) {
    if (a.length < matches.last+1) {
      a = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), matches.last+1);
    }

    int row = 0;
    for (; row <= matches.last; row++) {
      a[row] = matches.data[row][colidx];
    }
    
    if (a.length > row+1) {
      a[row+1] = null;
    }
    
    return a;
  }
  
  // -- object

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    
    Iterator i = iterator();
    boolean hasNext = i.hasNext();
    while (hasNext) {
      Object o = i.next();
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
  public Iterator iterator() {
    return new Iterator() {	

	protected int row = 0;

      @Override
	public boolean hasNext() {
	  return (row < (matches.last+1));
	}
	
      @Override
	public Object next() {
	  return matches.data[row++][colidx];
	}
	
      @Override
	public void remove() {
	  throw new UnsupportedOperationException();
	}
      };
  }
  
}
