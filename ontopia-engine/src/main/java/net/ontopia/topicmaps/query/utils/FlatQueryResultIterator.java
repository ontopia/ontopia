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

package net.ontopia.topicmaps.query.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * INTERNAL: Iterator which returns each object returned by the query,
 * checking each row against the previous to avoid duplicates.
 *
 * @since 2.0
 */
public class FlatQueryResultIterator implements Iterator  {

  private QueryResultIF result;
  private Object next;
  private int next_column;
  private Object[] previous_row;
  private boolean has_next;
  
  public FlatQueryResultIterator(QueryResultIF result) {
    this.result = result;
    this.next_column = 0;
    this.previous_row = new Object[result.getWidth()];
    this.has_next = result.next();
    this.next = findNext();
  }
  
  @Override
  public boolean hasNext() {
    return next != null;
  }
  
  @Override
  public Object next() {
    if (next == null) {
      throw new NoSuchElementException();
    }

    Object current = next;
    next = findNext();
    return current;
  }

  private Object findNext() {
    Object next = null;
    //! while (next == null && (next_column < result.getWidth() || has_next)) { // old check
    while (next == null && has_next && next_column < result.getWidth()) {
      next = result.getValue(next_column++);

      Object previous = previous_row[next_column - 1];
      if (next != null && previous != null && next.equals(previous)) {
        next = null;
      }
      
      if (next_column >= result.getWidth() && has_next) {
        next_column = 0;
        previous_row = result.getValues();
        has_next = result.next();
      }
    }

    return next;
  }
  
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}
