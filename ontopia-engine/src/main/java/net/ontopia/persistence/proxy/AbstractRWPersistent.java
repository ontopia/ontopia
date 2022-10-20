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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: An abstract PersistentIF implementation that handles most
 * of the machinery needed to implement persistent objects. Note that
 * other persistent classes should extend this class.
 */

public abstract class AbstractRWPersistent implements PersistentIF {
  
  protected IdentityIF id;
  protected TransactionIF txn;

  protected static final int[] MASKS;

  private static final byte STATE_NEW = 1; // object exists in database
  private static final byte STATE_IN_DATABASE = 2; // object exists in database
  private static final byte STATE_PERSISTENT = 4; // object persistent/created in transaction
  private static final byte STATE_DELETED = 8; // object deleted in transaction
  private static final byte STATE_HOLLOW = 16; // object retrieved in previous transaction(s)

  // bit-masks
  private int lflags; // is field specified (loaded)
  private int dflags; // is field dirty (not flushed)
  private int fflags; // is field dirty (flushed)
  private byte pstate; // new OR persistent/deleted OR in-database

  public Object[] values = new Object[_p_getFieldCount()]; // field values
  
  static {
    int[] masks = new int[32];
    for (int i=0; i < 32; i++) {
      masks[i] = (int)Math.pow(2, i);
    }
    MASKS = masks;
  }  

  public AbstractRWPersistent() {
  }

  public AbstractRWPersistent(TransactionIF txn) {
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

  public abstract void syncAfterMerge(IdentityIF source, IdentityIF target);

  protected void syncFieldsAfterMerge(IdentityIF source, IdentityIF target, int... fields) {
    for (int i = 0; i < fields.length; i++) {
      int field = fields[i];
      // only process loaded fields
      if (isLoaded(field)) {
        Object o = getValue(field);
        if (o instanceof AbstractRWPersistent) {
          // only process objects of same type
          if (((Class<?>)source.getType()).isAssignableFrom(o.getClass())) {
            ((AbstractRWPersistent) o).syncAfterMerge(source, target);
          }
        }
        if (o instanceof TrackableSet) {
          TrackableSet ts = (TrackableSet) o;
          if (ts.getAdded() != null) {
            for (Object sub : ts.getAdded()) {
              if (sub instanceof AbstractRWPersistent) {
                if (((Class<?>)source.getType()).isAssignableFrom(sub.getClass())) {
                  ((AbstractRWPersistent) sub).syncAfterMerge(source, target);
                }
              }
            }
          }
          if (ts.getRemoved() != null) {
            for (Object sub : ts.getRemoved()) {
              if (sub instanceof AbstractRWPersistent) {
                if (((Class<?>)source.getType()).isAssignableFrom(sub.getClass())) {
                  ((AbstractRWPersistent) sub).syncAfterMerge(source, target);
                }
              }
            }
          }
        }
      }
    }
    if (_p_getIdentity().equals(source)) {
      _p_setIdentity(target);
    }
  }

  // -----------------------------------------------------------------------------
  // PersistentIF machinery
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Called when the instance requests the initialization of
   * the specified field value. This call will lead to the value being
   * retrieved from the data repository if the instance is managed by
   * a transaction, otherwise a default value will be set.
   *
   * @return FIXME: only loaded values will be returned. if the
   * default value is being set, the return value is null.
   */
  protected <F> F loadField(int field) {
    // load field from storage
    if (isLoaded(field)) {
      return this.<F>getValue(field);

    } else if (isNewObject() || !isInDatabase()) {
      return null;

    } else {
      // get identity
      IdentityIF identity = _p_getIdentity();
      if (identity == null) {
        return null;
      }
      // load from storage
      F value = null;
      try { 
        value = txn.<F>loadField(identity, field);
      } catch (IdentityNotFoundException e) {
        // let value be null
      }
      // set value and mark field as loaded
      setValue(field, value);
      return value;
    }
  }

  // NOTE: method will throw IdentityNotFoundException if value object
  // not found
  protected <F> F loadFieldNoCheck(int field) throws IdentityNotFoundException {
    // load field from storage
    if (isLoaded(field)) {
      return this.<F>getValue(field);

    } else if (isNewObject() || !isInDatabase()) {
      return null;

    } else {
      // get identity
      IdentityIF identity = _p_getIdentity();
      if (identity == null) {
        return null;
      }
      // load from storage
      F value = txn.<F>loadField(identity, field);
      // set value and mark field as loaded
      setValue(field, value);
      return value;
    }
  }

  protected <F> Collection<F> loadCollectionField(int field) {
    // load field from storage
    if (isLoaded(field)) {
      Collection<F> o = this.<Collection<F>>getValue(field);
      return (o == null ? Collections.<F>emptySet() : o);

    } else if (isNewObject() || !isInDatabase()) {
      return Collections.EMPTY_SET;

    } else {
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
        setValue(field, coll);
        return Collections.EMPTY_SET;
      } else {
        // set value and mark field as loaded
        coll = new TrackableSet<F>(txn, coll);
        setValue(field, coll);
        return coll;
      }
    }
  }

  @Override
  public abstract void detach();

  protected void detachField(int field) {
    if (!isLoaded(field)) {
      loadField(field);
    }
  }

  protected void detachCollectionField(int field) {
    if (!isLoaded(field)) {
      loadCollectionField(field);
    }
  }

  /**
   * INTERNAL: Called when a field value has been changed. The
   * managing transaction will be notified.
   */
  protected void valueChanged(int field, Object value, boolean dchange) {
    // initialize state
    if (pstate == STATE_HOLLOW) { 
      IdentityIF identity = _p_getIdentity();
      if (identity != null) {
        txn._getObject(identity);
      } 
    }

    //! System.out.println(">>  " + field + " " + _p_getIdentity() + " " + value);    
    setValue(field, value); // set new value / replace value

    setDirty(field, true);      
    if (isPersistent()) {
      txn.objectDirty(this);
    }

    // if value is ContentReader then flush transaction immediately
    if (value instanceof OnDemandValue) {
      txn.flush();
    }
  }

  protected void valueAdded(int field, Object value, boolean dchange) {
    TrackableCollectionIF coll;
    if (isLoaded(field)) {
      coll = (TrackableCollectionIF)getValue(field);      
      if (coll == null) {
        coll = new TrackableSet(txn, null); // null == empty set
        setValue(field, coll);
      }
    } else if (isNewObject() || !isInDatabase()) {
      coll = new TrackableSet(txn, null); // null == empty set
      setValue(field, coll);
      
    } else {
      if (dchange) {
        Collection _coll = null;
        try {
          _coll = (Collection)txn.loadField(_p_getIdentity(), field);
        } catch (IdentityNotFoundException e) {
          // let coll be null
        }
        
        coll = new TrackableSet(txn, _coll);
      } else {
        coll = new TrackableLazySet(txn, _p_getIdentity(), field);
      }
      setValue(field, coll);
    }

    if (coll.addWithTracking(value)) {
      //! System.out.println(">>+ " + field + " " + _p_getIdentity() + " " + value);      
      setDirty(field, true);      
      if (isPersistent()) {
        txn.objectDirty(this);
      }
    }
  }

  protected void valueRemoved(int field, Object value, boolean dchange) {
    TrackableCollectionIF coll = null;
    if (isLoaded(field)) {
      coll = (TrackableCollectionIF)getValue(field);      

    } else if (isInDatabase()) {
      if (dchange) {
        Collection _coll = null;
        try {
          _coll = (Collection)txn.loadField(_p_getIdentity(), field);
        } catch (IdentityNotFoundException e) {
          // let coll be null
        }
        
        coll = new TrackableSet(txn, _coll);
      } else {
        coll = new TrackableLazySet(txn, _p_getIdentity(), field);
      }
      
      setValue(field, coll);
    }

    if (coll != null && coll.removeWithTracking(value)) {
      //! System.out.println(">>- " + field + " " + _p_getIdentity() + " " + value);
      setDirty(field, true);
      if (isPersistent()) {
        txn.objectDirty(this);
      }
    }
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
    return !(isPersistent() || isDeleted());
  }

  @Override
  public boolean isNewObject() {
    return ((pstate & STATE_NEW) == STATE_NEW);
  }
  
  @Override
  public void setNewObject(boolean newObject) {
    if (newObject) {
      pstate = STATE_NEW; // new (note == NEW)
    } else {
      pstate &= ~(STATE_NEW); // -new
    }
  }

  @Override
  public boolean isInDatabase() {
    return ((pstate & STATE_IN_DATABASE) == STATE_IN_DATABASE);
  }
  
  @Override
  public void setInDatabase(boolean inDatabase) {
    if (inDatabase) {
      pstate |= STATE_IN_DATABASE; // +new
    } else {
      pstate &= ~(STATE_IN_DATABASE); // -new
    }
  }

  @Override
  public boolean isPersistent() {
    return ((pstate & STATE_PERSISTENT) == STATE_PERSISTENT);
  }
  
  @Override
  public void setPersistent(boolean persistent) {
    if (persistent) {
      pstate |= STATE_PERSISTENT; // +persistent
      pstate &= ~(STATE_DELETED); // -deleted
    } else {
      pstate &= ~(STATE_PERSISTENT); // -persistent
    }
  }

  @Override
  public boolean isDeleted() {
    return ((pstate & STATE_DELETED) == STATE_DELETED);
  }
  
  @Override
  public void setDeleted(boolean deleted) {
    if (deleted) {
      pstate |= STATE_DELETED; // +deleted
      pstate &= ~(STATE_PERSISTENT); //a -persistent
    } else {
      pstate &= ~(STATE_DELETED); // -deleted
    }
  }

  // -- loaded

  @Override
  public boolean isLoaded(int field) {
    // initialize state
    if (pstate == STATE_HOLLOW) { 
      IdentityIF identity = _p_getIdentity();
      if (identity != null) {
        txn._getObject(identity);
      } 
    }

    return ((lflags & MASKS[field]) == MASKS[field]);
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
  
  protected <F> F getValue(int field) {
    return (F) values[field];
  }
  
  protected void setValue(int field, Object value) {
    lflags |= MASKS[field]; // set flag    
    values[field] = value;
  }
  
  protected void unsetValue(int field, Object value) {
    lflags &= ~(MASKS[field]); // unset flags
    dflags &= ~(MASKS[field]);
    fflags &= ~(MASKS[field]);
    values[field] = null;
  }

  // -- dirty (unflushed)

  @Override
  public boolean isDirty() {
    return (dflags != 0);
  }

  @Override
  public boolean isDirty(int field) {
    return ((dflags & MASKS[field]) == MASKS[field]);
  }

  @Override
  public int nextDirty(int start) {
    for (int i=start; i < values.length; i++) {
      if ((dflags & MASKS[i]) == MASKS[i]) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int nextDirty(int start, int end) {
    for (int i=start; i < end; i++) {
      if ((dflags & MASKS[i]) == MASKS[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public void setDirty(int field, boolean dirty) {
    if (dirty) {
      dflags |= MASKS[field]; // set flag    
    } else {
      dflags &= ~(MASKS[field]); // unset flag
    }
  }

  // -- dirty (flushed)

  public boolean isDirtyFlushed() {
    return (fflags != 0);
  }

  public boolean isDirtyFlushed(int field) {
    return ((fflags & MASKS[field]) == MASKS[field]);
  }

  public int nextDirtyFlushed(int start) {
    for (int i=start; i < values.length; i++) {
      if ((fflags & MASKS[i]) == MASKS[i]) {
        return i;
      }
    }
    return -1;
  }

  public int nextDirtyFlushed(int start, int end) {
    for (int i=start; i < end; i++) {
      if ((fflags & MASKS[i]) == MASKS[i]) {
        return i;
      }
    }
    return -1;
  }
  
  @Override
  public void setDirtyFlushed(int field, boolean dirty) {
            
    if (values[field] instanceof OnDemandValue) {
      OnDemandValue odv = (OnDemandValue)values[field];
      IdentityIF identity = _p_getIdentity();
      RDBMSMapping mapping = txn.getStorageAccess().getStorage().getMapping();
      ClassInfoIF cinfo = mapping.getClassInfo(identity.getType());
      FieldInfoIF finfo = cinfo.getValueFieldInfos()[field];
      odv.setContext(identity, finfo);
    }
    
    if (dirty) {
      fflags |= MASKS[field]; // set flag    
      dflags &= ~(MASKS[field]); // unset flag
    } else {
      fflags &= ~(MASKS[field]); // unset flag
    }
  }

  // -- misc

  @Override
  public void clearAll() {
    // reset flags
    lflags = 0;
    dflags = 0;
    fflags = 0;
    // reset state
    pstate = STATE_HOLLOW; // hollow / transient

    // clear field values
    for (int i=0; i < values.length; i++) {
      values[i] = null;
    }
  }

  public String _p_toString() {
    return states() + "\n  l:" + list(lflags) + "\n  d:" + list(dflags) + "\n  f:" + list(fflags) + "\n    " + 
      (values == null ? null : Arrays.asList(values).toString());
  }
  
  private String states() {
    return isNewObject() + "-" + isPersistent() + "-" + isDeleted() + "-" + isInDatabase();
  }

  private String list(int bitmask) {
    StringBuilder sb = new StringBuilder();
    for (int i=0; i < values.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append((bitmask & MASKS[i]) == MASKS[i]);
    }
    return sb.toString();
  }
  
}
