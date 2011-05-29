
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

  public IdentityIF _p_getIdentity() {
    return id;
  }

  public Object _p_getType() {
    return getClass();
  }
  
  public void _p_setIdentity(IdentityIF identity) {
    this.id = identity;
  }
  
  public TransactionIF _p_getTransaction() {
    return txn;
  }

  public void _p_setTransaction(TransactionIF txn) {
    if (this.txn != null)
      throw new OntopiaRuntimeException("Cannot change the transaction of a persistent object.");
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
  protected Object loadField(int field) {
    // get identity
    IdentityIF identity = _p_getIdentity();
    if (identity == null) return null;
    // load from storage
    try { 
      return txn.loadField(identity, field);
    } catch (IdentityNotFoundException e) {
      return null;
    }
  }

  // NOTE: method will throw IdentityNotFoundException if value object
  // not found
  protected Object loadFieldNoCheck(int field) throws IdentityNotFoundException {
    // get identity
    IdentityIF identity = _p_getIdentity();
    if (identity == null) return null;
    // load from storage
    return txn.loadField(identity, field);
  }

  protected Collection loadCollectionField(int field) {
    // get identity
    IdentityIF identity = _p_getIdentity();
    if (identity == null) return Collections.EMPTY_SET;
    // load from storage
    Object coll = null; 
    try {
      coll = txn.loadField(identity, field);
    } catch (IdentityNotFoundException e) {
      // let coll be null
    }
    if (coll == null) {
      return Collections.EMPTY_SET;
    } else {
      // set value and mark field as loaded
      return new ReadOnlySet(txn, (Collection)coll);
    }
  }

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

  public boolean isTransient() {
    return false;
  }

  public boolean isNewObject() {
    return false;
  }
  
  public void setNewObject(boolean newObject) {
    throw new UnsupportedOperationException();
  }

  public boolean isInDatabase() {
    return true;
  }
  
  public void setInDatabase(boolean inDatabase) {
    throw new UnsupportedOperationException();
  }

  public boolean isPersistent() {
    return true;
  }
  
  public void setPersistent(boolean persistent) {
    throw new UnsupportedOperationException();
  }

  public boolean isDeleted() {
    return false;
  }
  
  public void setDeleted(boolean deleted) {
    throw new UnsupportedOperationException();
  }

  // -- loaded

  public boolean isLoaded(int field) {
    return false;
  }

  // -- values

  public Object loadValue(FieldInfoIF finfo) {
    if (finfo.isCollectionField()) 
      return loadCollectionField(finfo.getIndex());
    else
      return loadField(finfo.getIndex());
  }

  // -- dirty (unflushed)

  public boolean isDirty() {
    return false;
  }

  public boolean isDirty(int field) {
    return false;
  }

  public int nextDirty(int start) {
    return -1;
  }

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
  
  public void setDirtyFlushed(int field, boolean dirty) {
    throw new UnsupportedOperationException();
  }

  // -- misc

  public void clearAll() {
    //! throw new UnsupportedOperationException();
  }

  public String _p_toString() {
    return "ReadOnly:" + _p_getIdentity();
  }
  
}
