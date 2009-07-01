// $Id: IteratorIterator.java,v 1.6 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: An iterator that works as a facade for multiple
 * iterators. The iterator represents the sum of all the
 * iterators.</p>
 */

public class IteratorIterator implements Iterator {

  protected Iterator colls_iter;
  protected Iterator iter;

  /**
   * @param colls_or_iters a collection of collections or iterators.
   */
  public IteratorIterator(Collection colls_or_iters) {
    colls_iter = colls_or_iters.iterator();
  }

  protected Iterator getNextIterator() {
    // Check to see if therre are any more collections
    if (colls_iter.hasNext()) {
    
      // Get next collection
      while (true) {
        Object coll_or_iter = colls_iter.next();
        Iterator iter;
        if (coll_or_iter instanceof Collection)
          iter = ((Collection)coll_or_iter).iterator();
        else
          iter = (Iterator)coll_or_iter;
        if (iter.hasNext())
            return iter;
        if (colls_iter.hasNext()) continue;
      }
    }
    return null;
  }
  
  public boolean hasNext() {
    if (iter != null) {
      // Check current iterator
      if (iter.hasNext()) return true;
    }

    // Get next iterator
    Iterator _iter = getNextIterator();
    if (_iter == null) return false;
    iter = _iter;
    return true;
  }

  public Object next() {
    return iter.next();
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}




