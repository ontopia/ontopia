package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Iterator;

import net.ontopia.topicmaps.query.core.QueryResultIF;

public class QueryResult implements QueryResultIF {

  private ResultSet result;
  private Iterator<Row> rowIterator;
  private Row currentRow;

  public QueryResult(ResultSet result) {
    this.result = result;
    this.rowIterator = result.iterator();
    this.currentRow = null;
  }

  public void close() {
    result = null;
  }

  public String getColumnName(int ix) {
    return result.getColumnName(ix);
  }

  public String[] getColumnNames() {
    String[] names = new String[result.getColumnCount()];
    for (int i = 0; i < result.getColumnCount(); i++) {
      names[i] = result.getColumnName(i);
    }
    return names;
  }

  public int getIndex(String colname) {
    return result.getColumnIndex(colname);
  }

  public Object getValue(int ix) {
    if (currentRow != null) {
      return currentRow.getValue(ix);
    } else {
      return null;
    }
  }

  public Object getValue(String colname) {
    int idx = result.getColumnIndex(colname);
    if (idx == -1) {
      throw new IllegalArgumentException("Column '" + colname
          + "' not existant in query result.");
    } else {
      return currentRow.getValue(idx);
    }
  }

  public Object[] getValues() {
    return currentRow.getValues();
  }

  public Object[] getValues(Object[] values) {
    return currentRow.getValues(values);
  }

  public int getWidth() {
    return result.getColumnCount();
  }

  public boolean next() {
    if (rowIterator.hasNext()) {
      currentRow = rowIterator.next();
      return true;
    } else {
      return false;
    }
  }
}
