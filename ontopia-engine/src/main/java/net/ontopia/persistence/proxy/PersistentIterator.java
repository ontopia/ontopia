
package net.ontopia.persistence.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Iterator that iterates over an iterator of IdentityIF
 * values and looks up the objects in the transaction. Any identities
 * that have been deleted will silently be ignored.
 */

public class PersistentIterator implements Iterator {

  final protected TransactionIF txn;
  final private boolean acceptDeleted; 
  final private Iterator iter;

  private int has_next = -1;
  private Object next;

  PersistentIterator(TransactionIF txn, boolean acceptDeleted, Iterator iter) {
    this.txn = txn;
    this.acceptDeleted = acceptDeleted;
    this.iter = iter;
  }

  public boolean hasNext() {
    if (has_next == 0)
      return false;
    else if (has_next == 1)
      return true;
    else {
      _next();
      return hasNext();
    }
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
    if (iter.hasNext())
      o = iter.next();
    else {
      has_next = 0;
      return;
    }
    // resolve object
    if (o == null) {
      has_next = 1;
      next = null;
    } else if (o instanceof IdentityIF) {
      try {
        o = txn.getObject((IdentityIF)o, acceptDeleted);
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
