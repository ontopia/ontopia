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

package net.ontopia.topicmaps.query.impl.rdbms;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * INTERNAL: The query result representation used by the basic implementation.
 */
public class QueryResult implements QueryResultIF {

  protected net.ontopia.persistence.proxy.QueryResultIF result;
  protected String[] colnames;

  protected int current;
  protected int last;
  
  public QueryResult(net.ontopia.persistence.proxy.QueryResultIF result, String[] colnames) {
    this(result, colnames, -1, -1);
  }

  public QueryResult(net.ontopia.persistence.proxy.QueryResultIF result, String[] colnames, int limit, int offset) {
    this.result = result;
    this.colnames = colnames;

    if (offset == -1) {
      offset = 0;
    }
    this.current = offset - 1;

    if (limit == -1) {
      this.last = Integer.MAX_VALUE;
    } else {
      this.last = Math.min(offset + limit, Integer.MAX_VALUE);
    }

    // Skip forward to initial offset
    for (int i=0; i < offset; i++) {
      if (!result.next()) {
        break;
      }
    }
  }

  // --- QueryResultIF implementation
    
  @Override
  public boolean next() {
    current++;
    if (current < last) {
      return result.next();
    } else {
      return false;
    }
  }

  @Override
  public Object getValue(int ix) {
    return result.getValue(ix);
  }
  
  @Override
  public Object getValue(String colname) {
    int index = getIndex(colname);
    if (index < 0) {
      throw new IndexOutOfBoundsException("No query result column named '" + colname + "'");
    }
    return result.getValue(index);
  }

  @Override
  public int getWidth() {
    return result.getWidth();
  }

  @Override
  public int getIndex(String colname) {
    if (colname != null) {
      for (int i=0; i < colnames.length; i++) {
        if (colname.equals(colnames[i])) {
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  public String[] getColumnNames() {
    return colnames;
  }

  @Override
  public String getColumnName(int ix) {
    return colnames[ix];
  }

  @Override
  public Object[] getValues() {
    return result.getValues();
  }

  @Override
  public Object[] getValues(Object[] values) {
    return result.getValues(values);
  }

  @Override
  public void close() {
    // Close underlying query result
    result.close();
  } 
}
