// $Id: CollectionCollection.java,v 1.6 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: A collection that works as a facade for multiple
 * collections. This class is a view onto the wrapped collections. The
 * view is the sum of all the collections.</p>
 */

public class CollectionCollection implements Collection {

  protected Collection colls;
  
  public CollectionCollection(Collection colls) {
    this.colls = colls;
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

    public boolean contains(Object o) {
    Iterator iter = colls.iterator();
    while (iter.hasNext()) {
      Collection coll = (Collection)iter.next();
      if (coll.contains(o)) return true;
    }
    return false;
  } 

  public boolean containsAll(Collection c) {
    Iterator iter = c.iterator();
    while (iter.hasNext()) {
      if (!contains(iter.next())) return false;
    }
    return true;
  } 

  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Collection)) return false;
    Iterator i1 = iterator();    
    Iterator i2 = ((Collection) o).iterator();
    while (i1.hasNext() && i2.hasNext()) {    
      Object o1 = i1.next();
      Object o2 = i2.next();
        if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
      }
    return !(i1.hasNext() || i2.hasNext());
  } 

  public boolean isEmpty() {
    Iterator iter = colls.iterator();
    while (iter.hasNext()) {
      Collection coll = (Collection)iter.next();
      if (!coll.isEmpty()) return false;
    }
    return true;
  } 

  public Iterator iterator() {
    return new IteratorIterator(colls);
  } 

  public int size() {
    int size = 0;
    Iterator iter = colls.iterator();
    while (iter.hasNext()) {
      Collection coll = (Collection)iter.next();
      size = size + coll.size();
    }
    return size;
  } 

  public Object[] toArray() {
    Object[] result = new Object[size()];
    Iterator e = iterator();
    for (int i=0; e.hasNext(); i++)
      result[i] = e.next();
    return result;
  } 

  public Object[] toArray(Object[] a) {
    int size = size();
    if (a.length < size)
      a = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    
    Iterator it=iterator();
    for (int i=0; i<size; i++)
      a[i] = it.next();
    
    if (a.length > size)
      a[size] = null;
    
    return a;
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

}




