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

package net.ontopia.topicmaps.query.impl.basic;

import java.util.AbstractList;
import java.util.List;

import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.ArrayMap;

/**
 * INTERNAL: Collection of QueryResult wrapper classes. These MapList
 * wrappers are being used by the TologQueryTag.
 */
public class QueryResultWrappers  {

  // --- external interface

  public static List getWrapper(QueryResultIF result) {
    return new ThreadSafeMapList((QueryResult) result);
  }
  
  private static class ThreadSafeMapList extends AbstractList  {

    protected Object[] coldefs;
    protected Object[][] data;
    protected int size;
    protected int offset;
    
    public ThreadSafeMapList(QueryResult result) {
      this(result.getColumnNames(), result.matches.data, result.last, result.current);
    }
      
    protected ThreadSafeMapList(Object[] coldefs, Object[][] data, int size,
                                int offset) {
      this.coldefs = coldefs;
      this.data = data;
      if (size == -1)
        this.size = 0;
      else
        this.size = size - (offset + 1);
      this.offset = offset;
    }
    
    // --- unmodifiable list implementation
    
    public Object get(int index) {
      // create new rowmap
      return new ArrayMap(coldefs, data[index + (offset + 1)]);
    }
    
    public int size() {
      return size;
    }
  }
  
}
