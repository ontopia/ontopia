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
import java.util.Map;
import java.util.NoSuchElementException;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * INTERNAL: Iterator that iterates over a QueryResultIF and returns a
 * new immutable Map instance for each query result row.
 *
 * @since 2.0
 */
public class QueryResultIterator implements Iterator<Map<String, Object>>  {

  protected QueryResultIF result;
  protected String[] keys;
  protected boolean has_next;
  
  public QueryResultIterator(QueryResultIF result) {
    this.result = result;
    keys = result.getColumnNames();
    has_next = result.next();
  }
  
  @Override
  public boolean hasNext() {
    return has_next;
  }
  
  @Override
  public Map<String, Object> next() {
    if (!has_next) {
      throw new NoSuchElementException();
    }
    ArrayMap<String, Object> rowmap = new ArrayMap<String, Object>(keys, result.getValues());
    has_next = result.next();
    return rowmap;
  }
  
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}
