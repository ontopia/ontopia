/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Vector;

import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * INTERNAL:  
 */
public class Row implements Cloneable {

  private Vector<Object> row;

  public Row(int cols) {
    row = new Vector<Object>(2 * cols);
    row.setSize(cols);
  }

  public int getColumnCount() {
    return row.size();
  }

  /**
   * 
   * @param column
   * @return
   * @throws IndexOutOfBoundsException
   */
  public Object getValue(int column) throws IndexOutOfBoundsException {
    if (column < 0 || column >= row.size()) {
      throw new IndexOutOfBoundsException("No column at index '" + column + "'");
    }
    return row.get(column);
  }

  public Object getFirstValue() {
    return row.get(0);
  }

  public Object getLastValue() {
    return row.get(row.size() - 1);
  }

  public void setValue(int column, Object val) {
    row.set(column, val);
  }
  
  public void setLastValue(Object val) {
    row.set(row.size() - 1, val);
  }

  public Object[] getValues() {
    return row.toArray();
  }

  public Object[] getValues(Object[] values) {
    return row.toArray(values);
  }

  public void addColumn() {
    row.setSize(row.size() + 1);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if ((obj == null) || (obj.getClass() != this.getClass()))
      return false;

    // object must be Row at this point
    Row other = (Row) obj;

    if (row.size() != other.row.size())
      return false;

    for (int idx = 0; idx < row.size(); idx++) {
      Object a = row.get(idx);
      Object b = other.row.get(idx);

      if (a == null && b == null)
        continue;
      if (a == null)
        return false;

      if (a instanceof TMObjectIF && b instanceof TMObjectIF) {
        String idA = ((TMObjectIF) a).getObjectId();
        String idB = ((TMObjectIF) b).getObjectId();

        if (idA == null && idB == null)
          continue;
        if (idA == null)
          return false;
        if (!idA.equals(idB))
          return false;
      } else {
        if (!a.equals(b))
          return false;
      }
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;

    for (Object col : row) {
      int val = 0;

      if (col != null) {
        if (col instanceof TMObjectIF) {
          val = ((TMObjectIF) col).getObjectId().hashCode();
        } else {
          val = col.hashCode();
        }
      }

      hash = 31 * hash + val;
    }
    return hash;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    Row r = (Row) super.clone();

    r.row = new Vector<Object>(this.row);
    return r;
  }
}
