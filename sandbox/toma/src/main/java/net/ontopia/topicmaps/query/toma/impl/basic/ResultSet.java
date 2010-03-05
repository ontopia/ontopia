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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;

import net.ontopia.utils.CompactHashSet;

/**
 * INTERNAL: This is a very simple implementation of a table, which is used to
 * represent matching result for TOMA queries.
 * <p>
 * The underlying data-structure is a {@link Bag}, which allows transparent
 * access to unique/duplicate rows.
 * </p>
 */
public class ResultSet implements Iterable<Row> {

  private Vector<String> columns;
  private Bag rows;
  private boolean unique;

  /**
   * Create an empty {@link ResultSet}, i.e. with zero columns.
   */
  public ResultSet() {
    this(0, false);
  }

  /**
   * Create a new {@link ResultSet} with the given number of columns.
   * 
   * @param cols the number of columns.
   * @param unique indicates whether this {@link ResultSet} should only contain
   *          unique rows, or allows duplicates.
   */
  public ResultSet(int cols, boolean unique) {
    columns = new Vector<String>(cols);
    columns.setSize(cols);
    rows = new HashBag();
    this.unique = unique;
  }

  /**
   * Create a new {@link ResultSet} that is based on the column definition from
   * one or more other {@link ResultSet}'s. The new {@link ResultSet} may
   * contain duplicate rows.
   * 
   * @param others the {@link ResultSet}'s
   */
  public ResultSet(ResultSet... others) {
    int cols = 0;
    for (ResultSet rs : others) {
      cols += rs.getColumnCount();
    }
    columns = new Vector<String>(cols);
    columns.setSize(cols);

    int i = 0;
    for (ResultSet rs : others) {
      for (int j = 0; j < rs.getColumnCount(); j++, i++) {
        setColumnName(i, rs.getColumnName(j));
      }
    }

    this.rows = new HashBag();
    this.unique = false;
  }

  /**
   * Returns an unmodifiable {@link List} of the column definitions.
   * 
   * @return an unmodifiable {@link List} of the columns.
   */
  public List<String> getColumnDefinitions() {
    return Collections.unmodifiableList(columns);
  }

  /**
   * Returns the name of the column at the given index.
   * 
   * @param index the column index.
   * @return the name of the column.
   * @throws IndexOutOfBoundsException if the index is outside the range of the
   *           column definition (e.g. index < 0 or index >= getColumnCount()).
   */
  public String getColumnName(int index) throws IndexOutOfBoundsException {
    if (index < 0 || index >= columns.size()) {
      throw new IndexOutOfBoundsException("No column available for index '"
          + index + "'");
    }
    return columns.get(index);
  }

  /**
   * Set the name of a specified column.
   * 
   * @param index the index of the column.
   * @param name the new name of the column.
   */
  public void setColumnName(int index, String name) {
    columns.set(index, name);
  }

  /**
   * Returns whether a column with the given name exists within this
   * {@link ResultSet}.
   * 
   * @param name the name of the column to look for.
   * @return true if a column with the given name exists; false otherwise.
   */
  public boolean containsColumn(String name) {
    return (getColumnIndex(name) > -1);
  }

  /**
   * Returns the number of columns contained in this {@link ResultSet}.
   * 
   * @return the number of columns.
   */
  public int getColumnCount() {
    return columns.size();
  }

  /**
   * Returns the index of the column with the given name.
   * 
   * @param name the name of the column to look for.
   * @return the index of the column, or -1 if no such column exists.
   */
  public int getColumnIndex(String name) {
    int idx = 0;
    for (String column : columns) {
      if (column.equals(name)) {
        return idx;
      }
      idx++;
    }
    return -1;
  }
  
  /**
   * Returns whether this {@link ResultSet} contains unique or duplicate rows.
   * 
   * @return true if this {@link ResultSet} only contains unique rows; false
   *         otherwise.
   */
  public boolean isUnique() {
    return unique;
  }

  /**
   * Set whether this {@link ResultSet} only allows unique rows, or can also
   * store duplicate ones.
   * 
   * @param unique if this {@link ResultSet} should only store unique rows, use
   *          true; false otherwise.
   */
  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  /**
   * Returns a {@link List} of all shared columns with the given
   * {@link ResultSet}.
   * 
   * @param rs the other {@link ResultSet}.
   * @return a {@link List} containing the shared columns.
   */
  public List<String> getSharedColumns(ResultSet rs) {
    List<String> sharedCols = new ArrayList<String>();

    List<String> vars = getBoundVariables();
    for (String var : vars) {
      if (rs.containsColumn(var)) {
        sharedCols.add(var);
      }
    }

    return sharedCols;
  }

  /**
   * Returns a {@link List} of variables that are present in this
   * {@link ResultSet}.
   * 
   * @return a {@link List} of variables in this {@link ResultSet}.
   */
  public List<String> getBoundVariables() {
    List<String> variables = new ArrayList<String>();
    for (String col : columns) {
      if (col.startsWith("$") && !col.contains(".")) {
        variables.add(col);
      }
    }
    return variables;
  }

  /**
   * Returns the number of rows that are stored in this {@link ResultSet}.
   * 
   * @return the number of rows.
   */
  public int getRowCount() {
    if (unique) {
      return rows.uniqueSet().size();
    } else {
      return rows.size();
    }
  }

  /**
   * Returns the index of the last column in this {@link ResultSet}. This is a
   * convenience method, and returns the same result as:
   * 
   * <pre>
   *   getColumnCount() - 1;
   * </pre>
   * 
   * @return the index of the last column.
   */
  public int getLastIndex() {
    return columns.size() - 1;
  }

  /**
   * Adds a new column to this {@link ResultSet}. The new column is appended at
   * the end of the existing columns. Every row, that is currently stored in
   * this {@link ResultSet} is adjusted to the new column size (filled with zero
   * values).
   * 
   * @param name the name of the new column.
   */
  public void addColumn(String name) {
    columns.add(name);
    for (Object r : rows) {
      ((Row) r).addColumn();
    }
  }

  /**
   * Returns an iterator over the rows of this {@link ResultSet}.
   * 
   * @return an iterator over all rows.
   */
  @SuppressWarnings("unchecked")
  public Iterator<Row> iterator() {
    if (unique) {
      return rows.uniqueSet().iterator();
    } else {
      return rows.iterator();
    }
  }

  /**
   * Return a new {@link Row} that is based on the column definitions of this
   * {@link ResultSet}.
   * <p>
   * <b>Note</b>: The returned {@link Row} is not added to the {@link ResultSet}.
   * 
   * @return a new {@link Row} that matches the column definitions of this
   *         {@link ResultSet}.
   */
  public Row createRow() {
    Row r = new Row(getColumnCount());
    return r;
  }

  /**
   * Adds a {@link Row} to this {@link ResultSet}.
   * 
   * @param row the {@link Row} to be added.
   */
  public void addRow(Row row) {
    rows.add(row);
  }

  /**
   * Adds all rows from the other {@link ResultSet} to this one.
   * <p>
   * <b>Note</b>: The layout of the two ResultSets has to be the same, otherwise
   * this operation will fail.
   * </p>
   * 
   * @param other the ResultSet to be added.
   */
  @SuppressWarnings("unchecked")
  public void addAll(ResultSet other) {
    rows.addAll(other.rows);
  }

  /**
   * Remove the given {@link Row} from this {@link ResultSet}.
   * 
   * @param row the {@link Row} to be removed.
   */
  public void removeRow(Row row) {
    rows.remove(row);
  }

  /**
   * Indicates whether the given {@link Row} is contained in this
   * {@link ResultSet}.
   * 
   * @param row the {@link Row} to be looked up.
   * @return true if the {@link Row} is contained in the {@link ResultSet};
   *         false otherwise.
   */
  public boolean containsRow(Row row) {
    return rows.contains(row);
  }

  /**
   * Remove all rows from this {@link ResultSet}.
   */
  public void removeAllRows() {
    rows.clear();
  }

  /**
   * Returns a unique {@link Collection} of all values in the specified column.
   *  
   * @param idx the column.
   * @return a {@link Collection} of values in that column.
   */
  @SuppressWarnings("unchecked")
  public Collection getValues(int idx) {
    Collection col = new CompactHashSet(rows.size());

    if (idx == -1)
      return col;

    for (Object r : rows.uniqueSet()) {
      Row row = (Row) r;
      Object val = row.getValue(idx);
      col.add(val);
    }

    return col;
  }

  /**
   * Returns all values in a specified column. This is a convenience method and
   * returns the same result as:
   *
   * <pre>
   *   getValues(getColumnIndex(name));
   * </pre>
   * 
   * @param column the column.
   * @return a {@link Collection} of values in that column.
   */
  @SuppressWarnings("unchecked")
  public Collection getValues(String column) {
    int idx = getColumnIndex(column);
    return getValues(idx);
  }
  
  /**
   * Returns all valid values from a specified column of this {@link ResultSet}. 
   * A valid value is a non-null value.
   * 
   * @param idx the column.
   * @return a {@link Collection} containing all valid values.
   */
  @SuppressWarnings("unchecked")
  public Collection getValidValues(int idx) {
    Collection col = new CompactHashSet(rows.size());

    if (idx == -1)
      return col;

    for (Object r : rows.uniqueSet()) {
      Row row = (Row) r;
      Object val = row.getValue(idx);
      if (val != null) {
        col.add(val);
      }
    }

    return col;
  }

  /**
   * Returns a new {@link ResultSet} that is the result of a merge operation of
   * the current {@link ResultSet} and the given one.
   * 
   * FIXME: this method has to be fixed, it does not produce correct results
   * for ResultSet that share more than one column.
   * 
   * @param rs the other {@link ResultSet} to be used for the merging.
   * @return a merged {@link ResultSet}.
   */
  @SuppressWarnings("unchecked")
  public ResultSet merge(ResultSet rs) {
    List<String> sharedCols = getSharedColumns(rs);
    if (sharedCols.isEmpty()) {
      return null;
    } else {
      String col = sharedCols.get(0);
      Collection vals1 = getValues(col);
      Collection vals2 = rs.getValues(col);

      vals1.addAll(vals2);
      ResultSet result = new ResultSet(1, true);
      result.setColumnName(0, col);

      for (Object o : vals1) {
        Row r = result.createRow();
        r.setLastValue(o);
        result.addRow(r);
      }

      return result;
    }
  }

  /**
   * Perform a union operation with the other {@link ResultSet}. As a result,
   * the current {@link ResultSet} will be extended with the rows from the other
   * {@link ResultSet}.
   * 
   * @param other the {@link ResultSet} that should be merged into the current
   *          {@link ResultSet}.
   * @param distinct indicates whether a distinct union operation should be
   *          performed or not.
   */
  public void union(ResultSet other, boolean distinct) {
    for (Row r : other) {
      if (!distinct || !containsRow(r)) {
        addRow(r);
      }
    }
  }

  /**
   * Perform an intersect operation with the other {@link ResultSet}. As a
   * result, the current {@link ResultSet} will be reduced to the intersection
   * of the two {@link ResultSet}'s.
   * 
   * @param other the {@link ResultSet} that should be intersected with the
   *          current {@link ResultSet}.
   */
  public void intersect(ResultSet other) {
    List<Row> toDelete = new ArrayList<Row>(other.getRowCount());
    for (Row r : this) {
      if (!other.containsRow(r)) {
        toDelete.add(r);
      }
    }
    rows.removeAll(toDelete);
  }

  /**
   * Perform an except operation with the other {@link ResultSet}. As a result,
   * the current {@link ResultSet} will be reduced with the rows from the other
   * {@link ResultSet}.
   * 
   * @param other the {@link ResultSet} that should be removed from the current
   *          {@link ResultSet}.
   */
  public void except(ResultSet other) {
    for (Row r : other) {
      removeRow(r);
    }
  }

  /**
   * Returns a {@link List} representation of this {@link ResultSet}.
   * 
   * @return a {@link List} containing all {@link Row} objects of the
   *         {@link ResultSet}.
   */
  public List<Row> getList() {
    List<Row> l = new ArrayList<Row>(getRowCount());
    for (Row r : this) {
      l.add(r);
    }
    return l;
  }
}
