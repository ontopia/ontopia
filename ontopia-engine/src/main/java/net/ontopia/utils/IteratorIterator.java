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

package net.ontopia.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * INTERNAL: An iterator that works as a facade for multiple
 * iterators. The iterator represents the sum of all the
 * iterators.</p>
 */

public class IteratorIterator<T> implements Iterator<T> {

  protected Iterator<Iterator<T>> colls_iter;
  protected Iterator<T> iter;

  /**
   * @param colls_or_iters a collection of collections or iterators.
   */
  public IteratorIterator(Collection<Collection<T>> colls_or_iters) {
    Collection<Iterator<T>> iterators = new ArrayList<Iterator<T>>(colls_or_iters.size());
    for (Collection<T> col : colls_or_iters) {
      iterators.add(col.iterator());
    }
    colls_iter = iterators.iterator();
  }

  public IteratorIterator(Iterator<Iterator<T>> colls_or_iters) {
    colls_iter = colls_or_iters;
  }

  protected Iterator<T> getNextIterator() {
    // Check to see if therre are any more collections
    if (colls_iter.hasNext()) {
    
      // Get next collection
      while (true) {
        Iterator<T> _iter = colls_iter.next();
        if (_iter.hasNext())
            return _iter;
        if (colls_iter.hasNext()) continue;
      }
    }
    return null;
  }
  
  public boolean hasNext() {
    if (iter != null) {
      // Check current iterator
      if (iter.hasNext()) return true;
    }

    // Get next iterator
    Iterator<T> _iter = getNextIterator();
    if (_iter == null) return false;
    iter = _iter;
    return true;
  }

  public T next() {
    return iter.next();
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}




