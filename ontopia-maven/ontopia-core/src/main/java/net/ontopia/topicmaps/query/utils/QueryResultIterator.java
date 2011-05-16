
// $Id: QueryResultIterator.java,v 1.6 2005/07/13 08:55:47 grove Exp $

package net.ontopia.topicmaps.query.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * INTERNAL: Iterator that iterates over a QueryResultIF and returns a
 * new immutable Map instance for each query result row.
 *
 * @since 2.0
 */
public class QueryResultIterator implements Iterator  {

  protected QueryResultIF result;
  protected Object[] keys;
  protected boolean has_next;
  
  public QueryResultIterator(QueryResultIF result) {
    this.result = result;
    keys = result.getColumnNames();
    has_next = result.next();
  }
  
  public boolean hasNext() {
    return has_next;
  }
  
  public Object next() {
    if (!has_next)
      throw new NoSuchElementException();
    ArrayMap rowmap = new ArrayMap(keys, result.getValues());
    has_next = result.next();
    return rowmap;
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}
