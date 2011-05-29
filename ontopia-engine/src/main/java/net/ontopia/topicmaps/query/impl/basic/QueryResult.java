
package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.parser.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The query result representation used by the basic implementation.
 */
public class QueryResult implements QueryResultIF {
  private static Logger log = LoggerFactory.getLogger(QueryResult.class.getName());

  QueryMatches matches;

  protected int current;
  protected int last;

  public QueryResult(QueryMatches matches) {
    this(matches, -1, -1);
  }

  public QueryResult(QueryMatches matches, int limit, int offset) {
    this.matches = matches;

    if (matches.last == -1) {
      this.current = -1;
      this.last = -1;
    } else {
      if (offset == -1)
        offset = 0;
      this.current = offset - 1;

      if (limit == -1)
        this.last = matches.last + 1;
      else
        this.last = Math.min(offset + limit, matches.last + 1);
    }
  }

  // --- QueryResultIF implementation
    
  public boolean next() {
    current++;
    return current < last;
  }

  public Object getValue(int ix) {
    return matches.data[current][ix];
  }
  
  public Object getValue(String colname) {
    int index = matches.getVariableIndex(colname);
    if (index < 0)
      throw new IndexOutOfBoundsException("No query result column named '" + colname + "'");
    return matches.data[current][index];
  }

  public int getWidth() {
    return matches.colcount;
  }

  public int getIndex(String colname) {
    return matches.getVariableIndex(colname);
  }

  public String[] getColumnNames() {
    String[] names = new String[matches.colcount];
    for (int ix = 0; ix < matches.colcount; ix++)
      names[ix] = ((Variable)matches.columnDefinitions[ix]).getName();
    return names;
  }

  public String getColumnName(int ix) {
    return ((Variable)matches.columnDefinitions[ix]).getName();
  }

  public Object[] getValues() {
    return matches.data[current];
  }
  
  public Object[] getValues(Object[] values) {
    Object[] row = matches.data[current];
    System.arraycopy(row, 0, values, 0, row.length);
    return values;
  }

  public void close() {
    matches = null; // lets the (potentially big) QueryMatches object be GC-ed
  }
}
