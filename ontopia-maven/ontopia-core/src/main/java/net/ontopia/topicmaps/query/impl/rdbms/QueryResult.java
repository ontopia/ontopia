
// $Id: QueryResult.java,v 1.12 2004/03/08 09:42:11 larsga Exp $

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

    if (offset == -1)
      offset = 0;
    this.current = offset - 1;

    if (limit == -1)
      this.last = Integer.MAX_VALUE;
    else
      this.last = Math.min(offset + limit, Integer.MAX_VALUE);

    // Skip forward to initial offset
    for (int i=0; i < offset; i++) {
      if (!result.next()) break;
    }
  }

  // --- QueryResultIF implementation
    
  public boolean next() {
    current++;
    if (current < last)
      return result.next();
    else
      return false;
  }

  public Object getValue(int ix) {
    return result.getValue(ix);
  }
  
  public Object getValue(String colname) {
    int index = getIndex(colname);
    if (index < 0)
      throw new IndexOutOfBoundsException("No query result column named '" + colname + "'");
    return result.getValue(index);
  }

  public int getWidth() {
    return result.getWidth();
  }

  public int getIndex(String colname) {
    if (colname != null) {
      for (int i=0; i < colnames.length; i++) {
        if (colname.equals(colnames[i]))
          return i;
      }
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
    return result.getValues();
  }

  public Object[] getValues(Object[] values) {
    return result.getValues(values);
  }

  public void close() {
    // Close underlying query result
    result.close();
  } 
}
