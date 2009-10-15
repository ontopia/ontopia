package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.List;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * PUBLIC: implementation of the {@link QueryResultIF} interface for the TOMA
 * QueryProcessor.
 */
public class QueryResult implements QueryResultIF {

  private List<String> columns;
  private List<Row> rows;
  private int currentRow;
  private int from;
  private int to;
  private boolean isClosed;

  /**
   * Create a new QueryResult instance that is backed by the given ResultSet.
   * 
   * @param result the ResultSet to be used.
   * @throws IllegalArgumentException if the given ResultSet is null.
   */
  protected QueryResult(List<String> columns, List<Row> rows, int limit, int offset)
      throws IllegalArgumentException {
    if (columns == null) {
      throw new IllegalArgumentException("Parameter columns for QueryResult may not be null.");
    }

    if (rows == null) {
      throw new IllegalArgumentException("Parameter rows for QueryResult may not be null.");
    }

    this.columns = columns;
    this.rows = rows;
    this.from = (offset == -1) ? 0 : offset;
    this.to = Math.min(rows.size() - 1, from + (limit == -1 ? Integer.MAX_VALUE : limit-1));
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
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public String getColumnName(int ix) throws IndexOutOfBoundsException,
      IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getColumnName() after QueryResult has been closed.");
    }
    
    return columns.get(ix);
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public String[] getColumnNames() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getColumnNames() after QueryResult has been closed.");
    }

    String[] names = new String[columns.size()];
    for (int i = 0; i < columns.size(); i++) {
      names[i] = columns.get(i);
    }
    return names;
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public int getIndex(String colname) throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getIndex() after QueryResult has been closed.");
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
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public Object getValue(int ix) throws IndexOutOfBoundsException,
      IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getValue() after QueryResult has been closed.");
    }

    if (currentRow == -1) {
      throw new IllegalStateException(
          "QueryResult is not pointed at a row anymore, call next() before using this method.");
    }

    return rows.get(currentRow).getValue(ix);
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public Object getValue(String colname) throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getValue() after QueryResult has been closed.");
    }

    if (currentRow == -1) {
      throw new IllegalStateException(
          "QueryResult is not pointed at a row anymore, call next() before using this method.");
    }
    
    int idx = getIndex(colname);
    if (idx == -1) {
      throw new IllegalArgumentException("Column '" + colname
          + "' not existant in QueryResult.");
    } else {
      return rows.get(currentRow).getValue(idx);
    }
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public Object[] getValues() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getValues() after QueryResult has been closed.");
    }

    if (currentRow == -1) {
      throw new IllegalStateException(
          "QueryResult is not pointed at a row anymore, call next() before using this method.");
    }
    
    return rows.get(currentRow).getValues();
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public Object[] getValues(Object[] values) throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getValues() after QueryResult has been closed.");
    }

    if (currentRow == -1) {
      throw new IllegalStateException(
          "QueryResult is not pointed at a row anymore, call next() before using this method.");
    }

    return rows.get(currentRow).getValues(values);
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public int getWidth() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getWidth() after QueryResult has been closed.");
    }
    
    return columns.size();
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public boolean next() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do next() after QueryResult has been closed.");
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
