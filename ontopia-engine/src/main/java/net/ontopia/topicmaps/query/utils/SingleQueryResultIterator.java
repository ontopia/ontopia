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
 * INTERNAL: Iterator that iterates over a QueryResultIF and returns
 * an immutable Map instance for each query result row. Note that the
 * Map instance is the same for each item, and that only the Map
 * <i>values</i> change with each step. This means that this iterator
 * must be used with care.
 */
public class SingleQueryResultIterator implements Iterator  {

  protected QueryResultIF result;
  protected ArrayMap rowmap;
  protected Object[] values;
  protected boolean has_next;
  
  public SingleQueryResultIterator(QueryResultIF result) {
    this.result = result;
    Object[] keys = result.getColumnNames();
    values = new Object[keys.length];
    rowmap = new ArrayMap(keys, values);
    has_next = result.next();
  }
  
  @Override
  public boolean hasNext() {
    return has_next;
  }
  
  @Override
  public Object next() {
    if (!has_next) {
      throw new NoSuchElementException();
    }
    result.getValues(values);
    has_next = result.next();
    return rowmap;
  }
  
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}
