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
 * INTERNAL: Representation of a single row in the ResultSet of a TOMA query.
 */
public class Row implements Cloneable {

  private Vector<Object> row;

  /**
   * Create a new {@link Row} to be used within a {@link ResultSet}.
   * 
   * @param cols the number of columns this {@link Row} should contain.
   */
  public Row(int cols) {
    row = new Vector<Object>(cols);
    row.setSize(cols);
  }

  /**
   * Returns the number of columns this {@link Row} contains.
   * 
   * @return the number of columns.
   */
  public int getColumnCount() {
    return row.size();
  }

  /**
   * Get the value of the i-th column in the current {@link Row}.
   * 
   * @param column the column to be used.
   * @return the value of the specified column.
   * @throws IndexOutOfBoundsException if the specified column is not contained
   *           in the {@link Row}.
   */
  public Object getValue(int column) throws IndexOutOfBoundsException {
    if (column < 0 || column >= row.size()) {
      throw new IndexOutOfBoundsException("No column at index '" + column + "'");
    }
    return row.get(column);
  }

  /**
   * Get the value of the first column of the current {@link Row}.
   * 
   * @return the value of the first column.
   */
  public Object getFirstValue() {
    return row.get(0);
  }

  /**
   * Get the value of the last column of the current {@link Row}.
   * This is a convenience method, equivalent to: 
   * 
   * <pre>
   *   getValue(getColumnCount() - 1);
   * </pre>
   * 
   * @return the value of the last column.
   */
  public Object getLastValue() {
    return row.get(row.size() - 1);
  }

  /**
   * Set the content at the specified column to a given value.
   *  
   * @param column the column to be used.
   * @param val the value to be set.
   */
  public void setValue(int column, Object val) {
    row.set(column, val);
  }
  
  /**
   * Set the value at the last column of the current {@link Row}.
   * This is a convenience method, equivalent to: 
   * 
   * <pre>
   *   setValue(getColumnCount() - 1, val);
   * </pre>
   * 
   * @param val the value to be set.
   */
  public void setLastValue(Object val) {
    row.set(row.size() - 1, val);
  }

  /**
   * Returns an array containing all the values of this {@link Row} in the
   * correct order.
   * 
   * @return the values stored in an array.
   */
  public Object[] getValues() {
    return row.toArray();
  }

  /**
   * Returns an array containing all the values of this {@link Row} in the
   * correct order. If the {@link Row} fits in the specified array with room to
   * spare (i.e., the array has more elements than the {@link Row}), the element
   * in the array immediately following the end of the {@link Row} is set to
   * null.
   * 
   * @param values the array into which the elements of the {@link Row} are to
   *          be stored, if it is big enough; otherwise, a new array of the same
   *          runtime type is allocated for this purpose.
   * @return an array containing the elements of the {@link Row}.
   */
  public Object[] getValues(Object[] values) {
    return row.toArray(values);
  }

  /**
   * Adds another column at the end of this {@link Row}. The element at the new
   * column position is set to null.
   */
  public void addColumn() {
    row.setSize(row.size() + 1);
  }

  /**
   * Fills this {@link Row} with the values from the given other {@link Row}'s.
   * <p>
   * <b>Note</b>: This method does not check if the column definition of the
   * {@link Row}'s match together.
   * </p>
   * 
   * @param rows the {@link Row}'s to be used to fill this {@link Row}.
   */
  public void fill(Row... rows) {
    int i = 0;
    for (Row r : rows) {
      for (int j = 0; j < r.getColumnCount(); j++, i++) {
        setValue(i, r.getValue(j));
      }
    }
  }
  
  /**
   * Indicates whether two objects of type {@link Row} are equal, i.e.
   * containing the same elements.
   * 
   * If the elements are of type {@link TMObjectIF}, their object id is compared.
   * 
   * @param obj the {@link Row} object to compare to.
   * @return true if the two {@link Row} objects are identical; false otherwise.
   */
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

  /**
   * Returns a clone of the current {@link Row} object. The result is a
   * shallow-copy of the underlying row container, i.e. the elements are not
   * duplicated but copied.
   * 
   * @return a shallow-copy of this {@link Row}.
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    Row r = (Row) super.clone();

    r.row = new Vector<Object>(this.row);
    return r;
  }
}
