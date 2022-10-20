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
 * INTERNAL: Query result representating queries have no variables and
 * evaluate to either true or false.
 */
public class BooleanQueryResult implements QueryResultIF {

  private int row = -1;
  private final int maxrow;
  private String[] colnames;
  private Object[] values;
  
  public BooleanQueryResult(String[] colnames, boolean result) {
    this(colnames, null, result);
  }

  public BooleanQueryResult(String[] colnames, Object[] values, boolean result) {
    if (result) {
      maxrow = 0;  // true -> one row
    } else {
      maxrow = -1; // false -> no rows
    }

    this.colnames = colnames;

    if (values == null) {
      this.values = new Object[colnames.length];
    } else {
      this.values = values;
    }
  }

  // --- QueryResultIF implementation
    
  @Override
  public boolean next() {
    if (row < maxrow) {
      row++;
      return true;
    } else {
      return false;
    }    
  }

  @Override
  public Object getValue(int ix) {
    return values[ix];
  }
  
  @Override
  public Object getValue(String colname) {
    int index = getIndex(colname);
    if (index < 0) {
      throw new IndexOutOfBoundsException("No query result column named '" + colname + "'");
    }
    return values[index];
  }

  @Override
  public int getWidth() {
    return colnames.length;
  }

  @Override
  public int getIndex(String colname) {
    for (int i = 0; i < colnames.length; i++) {
      if (colnames[i].equals(colname)) {
        return i;
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
    return values;
  }

  @Override
  public Object[] getValues(Object[] values) {
    System.arraycopy(this.values, 0, values, 0, this.values.length);
    return values;
  }

  @Override
  public void close() {
    // Nothing needs to be released.
  }

}
