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

import java.util.Collection;
import java.util.Collections;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: An abstract PersistentIF implementation that handles most
 * of the machinery needed to implement persistent objects. Note that
 * other persistent classes should extend this class.
 */

public abstract class AbstractROPersistent implements PersistentIF {
  
  protected IdentityIF id;
  protected TransactionIF txn;

  public AbstractROPersistent() {
  }

  public AbstractROPersistent(TransactionIF txn) {
    _p_setTransaction(txn);
  }
  
  // -----------------------------------------------------------------------------
  // PersistentIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public IdentityIF _p_getIdentity() {
    return id;
  }

  @Override
  public Class<?> _p_getType() {
    return getClass();
  }
  
  @Override
  public void _p_setIdentity(IdentityIF identity) {
    this.id = identity;
  }
  
  @Override
  public TransactionIF _p_getTransaction() {
    return txn;
  }

  @Override
  public void _p_setTransaction(TransactionIF txn) {
    if (this.txn != null) {
      throw new OntopiaRuntimeException("Cannot change the transaction of a persistent object.");
    }
    this.txn = txn;
  }
  
  // -----------------------------------------------------------------------------
  // PersistentIF machinery
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Called when the instance requests the value of the
   * specified field value. This call will lead to the value being
   * retrieved from the data repository.
   */
  protected <F> F loadField(int field) {
    // get identity
    IdentityIF identity = _p_getIdentity();
    if (identity == null) {
      return null;
    }
    // load from storage
    try { 
      return txn.<F>loadField(identity, field);
    } catch (IdentityNotFoundException e) {
      return null;
    }
  }

  // NOTE: method will throw IdentityNotFoundException if value object
  // not found
  protected <F> F loadFieldNoCheck(int field) throws IdentityNotFoundException {
    // get identity
    IdentityIF identity = _p_getIdentity();
    if (identity == null) {
      return null;
    }
    // load from storage
    return txn.<F>loadField(identity, field);
  }

  protected <F> Collection<F> loadCollectionField(int field) {
    // get identity
    IdentityIF identity = _p_getIdentity();
    if (identity == null) {
      return Collections.EMPTY_SET;
    }
    // load from storage
    Collection<F> coll = null; 
    try {
      coll = txn.<Collection<F>>loadField(identity, field);
    } catch (IdentityNotFoundException e) {
      // let coll be null
    }
    if (coll == null) {
      return Collections.EMPTY_SET;
    } else {
      // set value and mark field as loaded
      return new ReadOnlySet<F>(txn, coll);
    }
  }

  @Override
  public void detach() {
    throw new UnsupportedOperationException();
  }

  // -----------------------------------------------------------------------------
  // Queries
  // -----------------------------------------------------------------------------

  protected Object executeQuery(String name, Object[] params) {
    return txn.executeQuery(name, params);
  }
  
  // -----------------------------------------------------------------------------
  // Object data and metadata
  // -----------------------------------------------------------------------------

  // -- persistent state

  @Override
  public boolean isTransient() {
    return false;
  }

  @Override
  public boolean isNewObject() {
    return false;
  }
  
  @Override
  public void setNewObject(boolean newObject) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isInDatabase() {
    return true;
  }
  
  @Override
  public void setInDatabase(boolean inDatabase) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isPersistent() {
    return true;
  }
  
  @Override
  public void setPersistent(boolean persistent) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isDeleted() {
    return false;
  }
  
  @Override
  public void setDeleted(boolean deleted) {
    throw new UnsupportedOperationException();
  }

  // -- loaded

  @Override
  public boolean isLoaded(int field) {
    return false;
  }

  // -- values

  @Override
  public Object loadValue(FieldInfoIF finfo) {
    if (finfo.isCollectionField()) { 
      return loadCollectionField(finfo.getIndex());
    } else {
      return loadField(finfo.getIndex());
    }
  }

  // -- dirty (unflushed)

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public boolean isDirty(int field) {
    return false;
  }

  @Override
  public int nextDirty(int start) {
    return -1;
  }

  @Override
  public int nextDirty(int start, int end) {
    return -1;
  }
  
  public void setDirty(int field, boolean dirty) {
    throw new UnsupportedOperationException();
  }

  // -- dirty (flushed)

  public boolean isDirtyFlushed() {
    return false;
  }

  public boolean isDirtyFlushed(int field) {
    return false;
  }

  public int nextDirtyFlushed(int start) {
    return -1;
  }

  public int nextDirtyFlushed(int start, int end) {
    return -1;
  }
  
  @Override
  public void setDirtyFlushed(int field, boolean dirty) {
    throw new UnsupportedOperationException();
  }

  // -- misc

  @Override
  public void clearAll() {
    //! throw new UnsupportedOperationException();
  }

  public String _p_toString() {
    return "ReadOnly:" + _p_getIdentity();
  }
  
}
