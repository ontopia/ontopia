/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * INTERNAL: A wrapper class for presenting a collection view on
 * iterators. The wrapped iterator will be lazily traversed.</p>
 */

public class IteratorCollection<T> implements Collection<T> {

  protected Iterator<T> iterator;
  protected Collection<T> coll;
  protected boolean resolved;
  protected int iter_size = -1;
  protected int max_size = Integer.MAX_VALUE;
  
  public IteratorCollection(Iterator<T> iterator) {
    this.iterator = iterator;
    coll = new ArrayList<T>();
    if (iterator.hasNext() && max_size > 0)
      resolved = false;
    else
      resolved = true;
  }

  public IteratorCollection(Iterator<T> iterator, int size) {
    this(iterator);
    iter_size = size;
  }

  public IteratorCollection(Iterator<T> iterator, int size, int max_size) {
    this(iterator, size);
    if (max_size < 0)
      this.max_size = 0;
    else
      this.max_size = max_size;
  }

  protected synchronized T nextObject() {
    // Get next object in iterator
    T object = iterator.next();
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
    if (coll.contains((T)o)) return true;
    synchronized (this) {
      while (!resolved) {
        Object object = nextObject();
        if (object == o) return true;
      }
    }
    return false;
  } 

  public boolean containsAll(Collection<?> c) {
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

  public Iterator<T> iterator() {
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

  public boolean add(T o) {
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
