
package net.ontopia.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * INTERNAL: An iterator that works as a facade for multiple
 * iterators. The iterator represents the sum of all the
 * iterators.</p>
 */

public class IteratorIterator<T> implements Iterator<T> {

  protected Iterator<Iterator<T>> colls_iter;
  protected Iterator<T> iter;

  /**
   * @param colls_or_iters a collection of collections or iterators.
   */
  public IteratorIterator(Collection<Collection<T>> colls_or_iters) {
    Collection<Iterator<T>> iterators = new ArrayList<Iterator<T>>(colls_or_iters.size());
    for (Collection<T> col : colls_or_iters) {
      iterators.add(col.iterator());
    }
    colls_iter = iterators.iterator();
  }

  public IteratorIterator(Iterator<Iterator<T>> colls_or_iters) {
    colls_iter = colls_or_iters;
  }

  protected Iterator<T> getNextIterator() {
    // Check to see if therre are any more collections
    if (colls_iter.hasNext()) {
    
      // Get next collection
      while (true) {
        Iterator<T> _iter = colls_iter.next();
        if (_iter.hasNext())
            return _iter;
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
    Iterator<T> _iter = getNextIterator();
    if (_iter == null) return false;
    iter = _iter;
    return true;
  }

  public T next() {
    return iter.next();
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}




