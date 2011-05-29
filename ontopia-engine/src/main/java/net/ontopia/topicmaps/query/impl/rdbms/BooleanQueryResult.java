
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
    if (result)
      maxrow = 0;  // true -> one row
    else
      maxrow = -1; // false -> no rows

    this.colnames = colnames;

    if (values == null)
      this.values = new Object[colnames.length];
    else
      this.values = values;
  }

  // --- QueryResultIF implementation
    
  public boolean next() {
    if (row < maxrow) {
      row++;
      return true;
    } else
      return false;    
  }

  public Object getValue(int ix) {
    return values[ix];
  }
  
  public Object getValue(String colname) {
    int index = getIndex(colname);
    if (index < 0)
      throw new IndexOutOfBoundsException("No query result column named '" + colname + "'");
    return values[index];
  }

  public int getWidth() {
    return colnames.length;
  }

  public int getIndex(String colname) {
    for (int i = 0; i < colnames.length; i++) {
      if (colnames[i].equals(colname)) return i;
    }
    return -1;
  }

  public String[] getColumnNames() {
    return colnames;
  }

  public String getColumnName(int ix) {
    return colnames[ix];
  }

  public Object[] getValues() {
    return values;
  }

  public Object[] getValues(Object[] values) {
    System.arraycopy(this.values, 0, values, 0, this.values.length);
    return values;
  }

  public void close() {
    // Nothing needs to be released.
  }

}
