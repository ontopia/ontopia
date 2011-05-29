
package net.ontopia.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * INTERNAL: Extends CompactHashSet to make it synchronized. Saves
 * memory compared with having to wrap the set in a SynchronizedSet.
 */
public class SynchronizedCompactHashSet<E> extends CompactHashSet<E> {

  public SynchronizedCompactHashSet() {
    super();
  }

  public SynchronizedCompactHashSet(Collection c) {
    super(c);
  }
  
  public SynchronizedCompactHashSet(int size) {
    super(size);
  }

  public int size() {
    synchronized(this) { return super.size(); }
  }
  public boolean isEmpty() {
    synchronized(this) {return super.isEmpty();}
  }
  public boolean contains(Object o) {
    synchronized(this) {return super.contains((E)o);}
  }
  public Object[] toArray() {
    synchronized(this) {return super.toArray();}
  }
  public <E> E[] toArray(E[] a) {
    synchronized(this) {return super.toArray(a);}
  }

  public Iterator<E> iterator() {
    return super.iterator(); // Must be manually synched by user!
  }

  public boolean add(Object o) {
    synchronized(this) {return super.add(o);}
  }
  public boolean remove(Object o) {
    synchronized(this) {return super.remove((E)o);}
  }

  public boolean containsAll(Collection coll) {
    synchronized(this) {return super.containsAll(coll);}
  }
  public boolean addAll(Collection coll) {
    synchronized(this) {return super.addAll(coll);}
  }
  public boolean removeAll(Collection coll) {
    synchronized(this) {return super.removeAll(coll);}
  }
  public boolean retainAll(Collection coll) {
    synchronized(this) {return super.retainAll(coll);}
  }
  public void clear() {
    synchronized(this) {super.clear();}
  }
  public String toString() {
    synchronized(this) {return super.toString();}
  }

  public boolean equals(Object o) {
    synchronized(this) {return super.equals(o);}
  }
  public int hashCode() {
    synchronized(this) {return super.hashCode();}
  }
}
