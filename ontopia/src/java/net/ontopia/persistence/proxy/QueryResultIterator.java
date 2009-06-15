
// $Id: QueryResultIterator.java,v 1.7 2005/07/12 09:37:39 grove Exp $

package net.ontopia.persistence.proxy;

import java.util.Iterator;
import java.util.NoSuchElementException;
  
/**
 * INTERNAL: Interator wrapper class for QueryResultIFs. A column
 * index can be specified to iterate certain columns. The default
 * column is 0. The iterator does not currently support the remove()
 * method.
 */

public class QueryResultIterator implements Iterator {

  protected QueryResultIF result;
  protected int index;

  protected boolean has_next;
  
  public QueryResultIterator(QueryResultIF result) {
    this(result, 0);
  }
  
  public QueryResultIterator(QueryResultIF result, int index) {
    this.result = result;
    this.index = index;

    // check to see if there is a next element
    has_next = result.next();
  }
  
  public synchronized boolean hasNext() {
    return has_next;
  }
  
  public Object next() {
    if (!has_next)
      throw new NoSuchElementException();
    synchronized (this) {
      // return value at given index
      Object value = result.getValue(index);
      // skip to next row
      has_next = result.next();
      return value;
    }
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }

  /**
   * INTERNAL: Close iterator and release any resources held by it.
   */
  public void close() {
    // close query result
    if (result != null) {
      synchronized (this) {
	try {
	  result.close();
	} finally {
	  result = null;
	  has_next = false;
	}
      }
    }
  }
  
  protected void finalize() {
    close();
  }
  
}
