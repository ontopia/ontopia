
// $Id: IdentityCollectionWrapper.java,v 1.4 2006/07/07 09:01:34 grove Exp $

package net.ontopia.persistence.proxy;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * INTERNAL: A set implementation that wraps an identity collection
 * and presents the underlying collection as if it had PersistentIF
 * instances inside. All identity lookup is done lazily, and no state
 * is stored by this instance except for the current TransactionIF and
 * the wrapped identities collection.
 */
public class IdentityCollectionWrapper implements Collection {
  protected TransactionIF txn;
  protected Collection other;

  public IdentityCollectionWrapper(TransactionIF txn, Collection identities) {
    this.txn = txn;
    this.other = identities;
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
    return other.size();
  }

  public boolean isEmpty() {
    return other.isEmpty();
  }

  public boolean contains(Object o) {    
    return other.contains((o instanceof PersistentIF ? ((PersistentIF)o)._p_getIdentity() : o));
  }

  public boolean containsAll(Collection c) {
    Iterator e = c.iterator();
    while (e.hasNext()) {
      if(!contains(e.next()))
        return false;
    }
    return true;
  }

  public Object[] toArray() {
    Object[] result = new Object[size()];
    Iterator it = iterator();

    int i = 0;
    for (; it.hasNext(); i++) {
      result[i] = it.next();
    }
    if (i+1 < result.length) {
      Object[] r = new Object[i+1];
      System.arraycopy(result, 0, r, 0, i+1);
      return r;
    } else {
      return result;
    }
  }

  public Object[] toArray(Object[] a) {
    int size = size();
    if (a.length < size)
      a = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);

    int i = 0;
    Iterator it = iterator();
    for (; it.hasNext(); i++) {    
      a[i] = it.next();
    }
    
    if (a.length > i+1)
      a[i+1] = null;
    
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
    return new IdentityCollectionIterator(other.iterator());
  }

  class IdentityCollectionIterator implements Iterator {

    private Iterator iter;
    private int has_next = -1;
    private Object next;

    private IdentityCollectionIterator(Iterator iter) {
      this.iter = iter;
    }

    public boolean hasNext() {
      while (has_next == -1)
        _next(); // updates has_next

      return has_next == 1;
    }

    public Object next() {
      if (has_next == 0) {
        throw new NoSuchElementException();
      } else if (has_next == 1) {
        has_next = -1;
        return next;
      } else {
        _next();
        return next();
      }
    }

    public void _next() {
      // get object from iterator
      Object o;
      try {
        o = iter.next();
      } catch (NoSuchElementException e) {
        has_next = 0;
        return;
      }
      // resolve object
      if (o == null) {
        has_next = 1;
        next = null;
      } else if (o instanceof IdentityIF) {
        try {
          o = txn.getObject((IdentityIF)o, true);
          if (o == null) {
            _next();
          } else {
            has_next = 1;
            next = o;
          }
        } catch (Throwable t) {
          has_next = -1;
          next = null;
        }
      } else {
        has_next = 1;
        next = o;
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

  }
  
}
