
// $Id: QueryMatchesCollection.java,v 1.3 2005/07/13 08:55:33 grove Exp $

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: A set implementation that wraps an QueryMatches instance
 * and presents the values in one of the columns as a collection.
*/

public class QueryMatchesCollection implements Collection {

  protected QueryMatches matches;
  protected int colidx;

  public QueryMatchesCollection(QueryMatches matches, int colidx) {
    this.matches = matches;
    this.colidx = colidx;
  }
  
  // -- immutable collection

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public boolean add(Object o) {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(Collection c) {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  public boolean removeAll(Collection c) {
    throw new UnsupportedOperationException();
  }

  public boolean retainAll(Collection c) {
    throw new UnsupportedOperationException();
  }

  // -- other

  public int size() {
    return matches.last+1;
  }

  public boolean isEmpty() {
    return matches.isEmpty();
  }

  public boolean contains(Object o) {    
    // linear scan
    if (o == null) {
      for (int row = 0; row <= matches.last; row++) {
	if (matches.data[row][this.colidx] == null) return true;
      }
      return false;
    } else {
      for (int row = 0; row <= matches.last; row++) {
	if (o.equals(matches.data[row][this.colidx])) return true;
      }
      return false;
    }
  }

  public boolean containsAll(Collection c) {
    Iterator e = c.iterator();
    while (e.hasNext())
      if(!contains(e.next()))
	return false;
    
    return true;
  }

  public Object[] toArray() {
    Object[] result = new Object[matches.last];
    for (int row = 0; row <= matches.last; row++) {
      result[row] = matches.data[row][colidx];
    }
    return result;
  }

  public Object[] toArray(Object[] a) {
    if (a.length < matches.last+1)
      a = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), matches.last+1);

    int row = 0;
    for (; row <= matches.last; row++) {
      a[row] = matches.data[row][colidx];
    }
    
    if (a.length > row+1)
      a[row+1] = null;
    
    return a;
  }
  
  // -- object

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append("[");
    
    Iterator i = iterator();
    boolean hasNext = i.hasNext();
    while (hasNext) {
      Object o = i.next();
      buf.append(o == this ? "(this Collection)" : String.valueOf(o));
      hasNext = i.hasNext();
      if (hasNext)
	buf.append(", ");
    }
    
    buf.append("]");
    return buf.toString();
  }

  // -- iterator

  public Iterator iterator() {
    return new Iterator() {	

	protected int row = 0;

	public boolean hasNext() {
	  return (row < (matches.last+1));
	}
	
	public Object next() {
	  return matches.data[row++][colidx];
	}
	
	public void remove() {
	  throw new UnsupportedOperationException();
	}
      };
  }
  
}
