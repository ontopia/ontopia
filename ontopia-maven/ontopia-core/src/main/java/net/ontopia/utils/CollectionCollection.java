// $Id: CollectionCollection.java,v 1.6 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * INTERNAL: A collection that works as a facade for multiple
 * collections. This class is a view onto the wrapped collections. The
 * view is the sum of all the collections.</p>
 */

public class CollectionCollection<T> implements Collection<T> {

  protected Collection<Collection<T>> colls;
  
  public CollectionCollection(Collection<Collection<T>> colls) {
    this.colls = colls;
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public boolean contains(Object o) {
    Iterator<Collection<T>> iter = colls.iterator();
    while (iter.hasNext()) {
      Collection<T> coll = iter.next();
      if (coll.contains(o)) return true;
    }
    return false;
  } 

  public boolean containsAll(Collection<?> c) {
    Iterator<?> iter = c.iterator();
    while (iter.hasNext()) {
      if (!contains((T)iter.next())) return false;
    }
    return true;
  } 

  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Collection)) return false;
    Iterator<T> i1 = iterator();    
    Iterator<T> i2 = ((Collection<T>) o).iterator();
    while (i1.hasNext() && i2.hasNext()) {    
      Object o1 = i1.next();
      Object o2 = i2.next();
        if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
      }
    return !(i1.hasNext() || i2.hasNext());
  } 

  public boolean isEmpty() {
    Iterator<Collection<T>> iter = colls.iterator();
    while (iter.hasNext()) {
      Collection<T> coll = iter.next();
      if (!coll.isEmpty()) return false;
    }
    return true;
  } 

  public Iterator<T> iterator() {
    return new IteratorIterator<T>(colls);
  } 

  public int size() {
    int size = 0;
    Iterator<Collection<T>> iter = colls.iterator();
    while (iter.hasNext()) {
      Collection<T> coll = iter.next();
      size = size + coll.size();
    }
    return size;
  } 

  public Object[] toArray() {
    Object[] result = new Object[size()];
    Iterator<T> e = iterator();
    for (int i=0; e.hasNext(); i++)
      result[i] = e.next();
    return result;
  } 

  public <T extends Object> T[] toArray(T[] a) {
    int size = size();
    if (a.length < size)
      a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    
    Iterator it=iterator();
    for (int i=0; i<size; i++)
      a[i] = (T)it.next();
    
    if (a.length > size)
      a[size] = null;
    
    return a;
  } 

  public boolean add(T o) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  } 

  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  } 

  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  } 

  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  } 

}




