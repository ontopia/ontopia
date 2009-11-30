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

import java.util.List;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * PUBLIC: implementation of the {@link QueryResultIF} interface for the TOMA
 * {@link BasicQueryProcessor}.
 */
public class QueryResult implements QueryResultIF {

  private List<String> columns;
  private List<Row> rows;
  private int currentRow;
  private int from;
  private int to;
  private boolean isClosed;

  /**
   * Create a new {@link QueryResult} instance that is backed by the given
   * ResultSet.
   * 
   * @param result the {@link ResultSet} to be used.
   * @throws IllegalArgumentException if the given {@link ResultSet} is null.
   */
  protected QueryResult(List<String> columns, List<Row> rows, int limit,
      int offset) throws IllegalArgumentException {
    if (columns == null) {
      throw new IllegalArgumentException("Parameter 'columns' may not be null.");
    }

    if (rows == null) {
      throw new IllegalArgumentException("Parameter 'rows' may not be null.");
    }

    this.columns = columns;
    this.rows = rows;
    this.from = (offset == -1) ? 0 : offset;
    this.to = Math.min(rows.size() - 1, from
        + (limit == -1 ? Integer.MAX_VALUE : limit - 1));
    this.currentRow = -1;
    this.isClosed = false;
  }

  public void close() {
    rows.clear();
    rows = null;
    columns = null;
    isClosed = true;
  }

  /**
   * @throws IllegalStateException if this {@link QueryResult} has already been
   *           closed.
   */
  public String getColumnName(int ix) throws IndexOutOfBoundsException,
      IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getColumnName() after query result has already been closed.");
    }

    return columns.get(ix);
  }

  /**
   * @throws IllegalStateException if this {@link QueryResult} has already been
   *           closed.
   */
  public String[] getColumnNames() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getColumnNames() after query result has already been closed.");
    }

    String[] names = new String[columns.size()];
    for (int i = 0; i < columns.size(); i++) {
      names[i] = columns.get(i);
    }
    return names;
  }

  /**
   * @throws IllegalStateException if this {@link QueryResult} has already been
   *           closed.
   */
  public int getIndex(String colname) throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getIndex() after query result has already been closed.");
    }

    int idx = 0;
    for (String column : columns) {
      if (column.equals(colname)) {
        return idx;
      }
      idx++;
    }
    return -1;
  }

  /**
   * @throws IllegalStateException if this {@link QueryResult} has already been
   *           closed.
   */
  public Object getValue(int ix) throws IndexOutOfBoundsException,
      IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getValue() after query result has already been closed.");
    }

    if (currentRow == -1) {
      throw new IllegalStateException(
          "The query result is not pointed at a row anymore, " +
          "call next() before using this method.");
    }

    return rows.get(currentRow).getValue(ix);
  }

  /**
   * @throws IllegalStateException if this {@link QueryResult} has already been
   *           closed.
   */
  public Object getValue(String colname) throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getValue() after query result has already been closed.");
    }

    if (currentRow == -1) {
      throw new IllegalStateException(
          "The query result is not pointed at a row anymore, " +
          "call next() before using this method.");
    }

    int idx = getIndex(colname);
    if (idx == -1) {
      throw new IllegalArgumentException("Column '" + colname
          + "' not existant in this query result.");
    } else {
      return rows.get(currentRow).getValue(idx);
    }
  }

  /**
   * @throws IllegalStateException if this {@link QueryResult} has already been
   *           closed.
   */
  public Object[] getValues() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getValues() after query result has already been closed.");
    }

    if (currentRow == -1) {
      throw new IllegalStateException(
          "The query result is not pointed at a row anymore, " +
          "call next() before using this method.");
    }

    return rows.get(currentRow).getValues();
  }

  /**
   * @throws IllegalStateException if this {@link QueryResult} has already been
   *           closed.
   */
  public Object[] getValues(Object[] values) throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getValues() after query result has already been closed.");
    }

    if (currentRow == -1) {
      throw new IllegalStateException(
          "The query result is not pointed at a row anymore, " +
          "call next() before using this method.");
    }

    return rows.get(currentRow).getValues(values);
  }

  /**
   * @throws IllegalStateException if this {@link QueryResult} has already been
   *           closed.
   */
  public int getWidth() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getWidth() after query result has already been closed.");
    }

    return columns.size();
  }

  /**
   * @throws IllegalStateException if this {@link QueryResult} has already been
   *           closed.
   */
  public boolean next() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do next() after query result has already been closed.");
    }

    if (currentRow == -1) {
      currentRow = from;
      if (currentRow <= to) {
        return true;
      } else {
        return false;
      }
    } else {
      if (++currentRow <= to) {
        return true;
      } else {
        return false;
      }
    }
  }
}
