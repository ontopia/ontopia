package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Iterator;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * INTERNAL: implementation of the {@link QueryResultIF} interface for the TOMA
 * QueryProcessor.
 */
public class QueryResult implements QueryResultIF {

  private ResultSet result;
  private Iterator<Row> rowIterator;
  private Row currentRow;
  private boolean isClosed;

  /**
   * Create a new QueryResult instance that is backed by the given ResultSet.
   * 
   * @param result the ResultSet to be used.
   * @throws IllegalArgumentException if the given ResultSet is null.
   */
  public QueryResult(ResultSet result) throws IllegalArgumentException {
    if (result == null) {
      throw new IllegalArgumentException("ResultSet must be non-null.");
    }
    
    this.result = result;
    this.rowIterator = result.iterator();
    this.currentRow = null;
    this.isClosed = false;
  }

  public void close() {
    currentRow = null;
    rowIterator = null;
    result = null;
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
    
    return result.getColumnName(ix);
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public String[] getColumnNames() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getColumnNames() after QueryResult has been closed.");
    }

    String[] names = new String[result.getColumnCount()];
    for (int i = 0; i < result.getColumnCount(); i++) {
      names[i] = result.getColumnName(i);
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
    
    return result.getColumnIndex(colname);
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

    if (currentRow == null) {
      throw new IllegalStateException(
          "QueryResult is not pointed at a row anymore, call next() before using this method.");
    }

    return currentRow.getValue(ix);
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public Object getValue(String colname) throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getValue() after QueryResult has been closed.");
    }

    if (currentRow == null) {
      throw new IllegalStateException(
          "QueryResult is not pointed at a row anymore, call next() before using this method.");
    }
    
    int idx = result.getColumnIndex(colname);
    if (idx == -1) {
      throw new IllegalArgumentException("Column '" + colname
          + "' not existant in QueryResult.");
    } else {
      return currentRow.getValue(idx);
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

    if (currentRow == null) {
      throw new IllegalStateException(
          "QueryResult is not pointed at a row anymore, call next() before using this method.");
    }
    
    return currentRow.getValues();
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public Object[] getValues(Object[] values) throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getValues() after QueryResult has been closed.");
    }

    if (currentRow == null) {
      throw new IllegalStateException(
          "QueryResult is not pointed at a row anymore, call next() before using this method.");
    }

    return currentRow.getValues(values);
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public int getWidth() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do getWidth() after QueryResult has been closed.");
    }
    
    return result.getColumnCount();
  }

  /**
   * @throws IllegalStateException if the QueryResult has been closed already.
   */
  public boolean next() throws IllegalStateException {
    if (isClosed) {
      throw new IllegalStateException(
          "Can't do next() after QueryResult has been closed.");
    }
    
    if (rowIterator.hasNext()) {
      currentRow = rowIterator.next();
      return true;
    } else {
      return false;
    }
  }
}
