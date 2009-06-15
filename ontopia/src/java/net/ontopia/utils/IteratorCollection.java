
// $Id: IteratorCollection.java,v 1.12 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: A wrapper class for presenting a collection view on
 * iterators. The wrapped iterator will be lazily traversed.</p>
 */

public class IteratorCollection implements Collection {

  protected Iterator iterator;
  protected Collection coll;
  protected boolean resolved;
  protected int iter_size = -1;
  protected int max_size = Integer.MAX_VALUE;
  
  public IteratorCollection(Iterator iterator) {
    this.iterator = iterator;
    coll = new ArrayList();
    if (iterator.hasNext() && max_size > 0)
      resolved = false;
    else
      resolved = true;
  }

  public IteratorCollection(Iterator iterator, int size) {
    this(iterator);
    iter_size = size;
  }

  public IteratorCollection(Iterator iterator, int size, int max_size) {
    this(iterator, size);
    if (max_size < 0)
      this.max_size = 0;
    else
      this.max_size = max_size;
  }

  protected synchronized Object nextObject() {
    // Get next object in iterator
    Object object = iterator.next();
    // Set resolved flag to true if this was the last object
    int csize = coll.size();
    if (!iterator.hasNext() || csize >= max_size || (iter_size > 0 && csize >= iter_size)) {
      resolved = true;
      iterator = null;
    }
    // Add object to collection
    coll.add(object);
    // Return the object
    return object;
  }

  protected synchronized void resolve() {
    while (iterator.hasNext()) {
      int csize = coll.size();
      if (csize >= max_size || (iter_size > 0 && csize >= iter_size)) break;
      coll.add(iterator.next());
    }
    resolved = true;
    iterator = null;
  }
  
  public void clear() {
    coll.clear();
    resolved = true;
    iterator = null;
  } 

  public boolean contains(Object o) {
    if (coll.contains(o)) return true;
    synchronized (this) {
      while (!resolved) {
        Object object = nextObject();
        if (object == o) return true;
      }
    }
    return false;
  } 

  public boolean containsAll(Collection c) {
    // FIXME: This one can be improved.
    // If partially resolved collection contains all objects then we're fine
    if (coll.containsAll(c)) return true;
    // Otherwise resolve iterator
    if (!resolved) resolve();
    // Call method on nested collection
    return coll.containsAll(c);
  } 

  public boolean isEmpty() {
    // Collection is empty if; built collection or iterator contains elements
    if (coll.size() > 0) return false;
    if (!resolved && iterator.hasNext() && max_size > 0) return false;
    return true;
  } 

  public Iterator iterator() {
    // FIXME: This can be improved lot by lazily traversing internal iterator.
    if (!resolved) resolve();
    return coll.iterator();
  } 

  public int size() {
    // If iterator has been resolved return the size of the collection
    if (resolved)
      return coll.size();
    // If iterator has explicit size use that.
    else if (iter_size >= 0) {
      if (iter_size < max_size)
        return iter_size;
      else
        return max_size;
    }
    // Last alternative is to traverse the entire iterator.
    resolve();
    return coll.size();
  } 

  // --- Methods that require the entire iterator to be traversed.
  
  public int hashCode() {
    // Traverse the entire iterator
    if (!resolved) resolve();
    // Call method on nested collection
    return coll.hashCode();
  } 

  public boolean equals(Object o) {
    if (!(o instanceof Collection))
      return false;

    // Traverse the entire iterator
    if (!resolved) resolve();
    // Call method on nested collection
    return coll.equals(o);
  } 

  public Object[] toArray() {
    // Traverse the entire iterator
    if (!resolved) resolve();
    // Call method on nested collection
    return coll.toArray();    
  } 

  public Object[] toArray(Object[] a) {
    // Traverse the entire iterator
    if (!resolved) resolve();
    // Call method on nested collection
    return coll.toArray(a);    
  } 

  public boolean add(Object o) {
    // Traverse the entire iterator
    if (!resolved) resolve();
    // Call method on nested collection
    return coll.add(o);
  }
  
  public boolean addAll(Collection c) {
    // Traverse the entire iterator
    if (!resolved) resolve();
    // Call method on nested collection
    return coll.addAll(c);
  } 

  public boolean remove(Object o) {
    // Traverse the entire iterator
    if (!resolved) resolve();
    // Call method on nested collection
    return coll.remove(o);
  } 

  public boolean removeAll(Collection c) {
    // Traverse the entire iterator
    if (!resolved) resolve();
    // Call method on nested collection
    return coll.removeAll(c);
  } 

  public boolean retainAll(Collection c) {
    // Traverse the entire iterator
    if (!resolved) resolve();
    // Call method on nested collection
    return coll.retainAll(c);
  } 

}
