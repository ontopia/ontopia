
package net.ontopia.topicmaps.query.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * INTERNAL: Iterator which returns each object returned by the query,
 * checking each row against the previous to avoid duplicates.
 *
 * @since 2.0
 */
public class FlatQueryResultIterator implements Iterator  {

  private QueryResultIF result;
  private Object next;
  private int next_column;
  private Object[] previous_row;
  private boolean has_next;
  
  public FlatQueryResultIterator(QueryResultIF result) {
    this.result = result;
    this.next_column = 0;
    this.previous_row = new Object[result.getWidth()];
    this.has_next = result.next();
    this.next = findNext();
  }
  
  public boolean hasNext() {
    return next != null;
  }
  
  public Object next() {
    if (next == null)
      throw new NoSuchElementException();

    Object current = next;
    next = findNext();
    return current;
  }

  private Object findNext() {
    Object next = null;
    //! while (next == null && (next_column < result.getWidth() || has_next)) { // old check
    while (next == null && has_next && next_column < result.getWidth()) {
      next = result.getValue(next_column++);

      Object previous = previous_row[next_column - 1];
      if (next != null && previous != null && next.equals(previous))
        next = null;
      
      if (next_column >= result.getWidth() && has_next) {
        next_column = 0;
        previous_row = result.getValues();
        has_next = result.next();
      }
    }

    return next;
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}
