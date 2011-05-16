
// $Id: PersistentObjectAccess.java,v 1.5 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.proxy;


/**
 * INTERNAL: Object access for objects implementing the PersistentIF
 * interface.
 */

public class PersistentObjectAccess implements ObjectAccessIF {

  protected TransactionIF txn;
  
  public PersistentObjectAccess(TransactionIF txn) {
    this.txn = txn;
  }

  public Object getObject(IdentityIF identity) {
    return txn.getObject(identity);
  }
  
  public IdentityIF getIdentity(Object object) {
    if (object == null)
      return null;
    else
      return ((PersistentIF)object)._p_getIdentity();
  }

  public Object getType(Object object) {
    return ((PersistentIF)object)._p_getType();
  }

  //! public Object getValue(Object object, int field) {
  //!   return ((PersistentIF)object).loadValue(field);
  //! }
  public Object getValue(Object object, FieldInfoIF finfo) {
    
    return ((PersistentIF)object).loadValue(finfo);
  }
  
  //! public void setValue(Object object, int field, Object value) {
  //!   throw new UnsupportedOperationException();
  //! }
  //! 
  //! public void addValue(Object object, int field, Object value) {
  //!   IdentityIF identity = ((PersistentIF)object)._p_getIdentity();
  //!   dcache.valueAdded(identity, field, value);
  //! }
  //! 
  //! public void removeValue(Object object, int field, Object value) {
  //!   IdentityIF identity = ((PersistentIF)object)._p_getIdentity();
  //!   dcache.valueRemoved(identity, field, value);
  //! }

  public boolean isDirty(Object object) {
    return ((PersistentIF)object).isDirty();
  }

  public boolean isDirty(Object object, int field) {
    return ((PersistentIF)object).isDirty(field);
  }

  public int nextDirty(Object object, int start) {
    return ((PersistentIF)object).nextDirty(start);
  }

  public int nextDirty(Object object, int start, int end) {
    return ((PersistentIF)object).nextDirty(start, end);
  }
  
  public void setDirtyFlushed(Object object, int field) {
    ((PersistentIF)object).setDirtyFlushed(field, true);
  }
  
}
