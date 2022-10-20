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

package net.ontopia.persistence.proxy;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * INTERNAL: Iterator that iterates over an iterator of IdentityIF
 * values and looks up the objects in the transaction. Any identities
 * that have been deleted will silently be ignored.
 */

public class PersistentIterator<E> implements Iterator<E> {

  final protected TransactionIF txn;
  final private boolean acceptDeleted; 
  final private Iterator<?> iter;

  private int has_next = -1;
  private E next;

  PersistentIterator(TransactionIF txn, boolean acceptDeleted, Iterator<?> iter) {
    this.txn = txn;
    this.acceptDeleted = acceptDeleted;
    this.iter = iter;
  }

  @Override
  public boolean hasNext() {
    if (has_next == 0) {
      return false;
    } else if (has_next == 1) {
      return true;
    } else {
      _next();
      return hasNext();
    }
  }

  @Override
  public E next() {
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
    if (iter.hasNext()) {
      o = iter.next();
    } else {
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
          next = (E) o;
        }
      } catch (Throwable t) {
        has_next = -1;
        next = null;
      }
    } else {
      has_next = 1;
      next = (E) o;
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
