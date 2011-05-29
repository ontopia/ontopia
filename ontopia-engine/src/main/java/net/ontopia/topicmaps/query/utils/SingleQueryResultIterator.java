
package net.ontopia.topicmaps.query.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * INTERNAL: Iterator that iterates over a QueryResultIF and returns
 * an immutable Map instance for each query result row. Note that the
 * Map instance is the same for each item, and that only the Map
 * <i>values</i> change with each step. This means that this iterator
 * must be used with care.
 */
public class SingleQueryResultIterator implements Iterator  {

  protected QueryResultIF result;
  protected ArrayMap rowmap;
  protected Object[] values;
  protected boolean has_next;
  
  public SingleQueryResultIterator(QueryResultIF result) {
    this.result = result;
    Object[] keys = result.getColumnNames();
    values = new Object[keys.length];
    rowmap = new ArrayMap(keys, values);
    has_next = result.next();
  }
  
  public boolean hasNext() {
    return has_next;
  }
  
  public Object next() {
    if (!has_next)
      throw new NoSuchElementException();
    result.getValues(values);
    has_next = result.next();
    return rowmap;
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}
