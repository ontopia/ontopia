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


/**
 * INTERNAL: Object access for objects implementing the PersistentIF
 * interface.
 */

public class PersistentObjectAccess implements ObjectAccessIF {

  protected TransactionIF txn;
  
  public PersistentObjectAccess(TransactionIF txn) {
    this.txn = txn;
  }

  @Override
  public Object getObject(IdentityIF identity) {
    return txn.getObject(identity);
  }
  
  @Override
  public IdentityIF getIdentity(Object object) {
    if (object == null) {
      return null;
    } else {
      return ((PersistentIF)object)._p_getIdentity();
    }
  }

  @Override
  public Class<?> getType(Object object) {
    return ((PersistentIF)object)._p_getType();
  }

  //! public Object getValue(Object object, int field) {
  //!   return ((PersistentIF)object).loadValue(field);
  //! }
  @Override
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

  @Override
  public boolean isDirty(Object object) {
    return ((PersistentIF)object).isDirty();
  }

  @Override
  public boolean isDirty(Object object, int field) {
    return ((PersistentIF)object).isDirty(field);
  }

  @Override
  public int nextDirty(Object object, int start) {
    return ((PersistentIF)object).nextDirty(start);
  }

  @Override
  public int nextDirty(Object object, int start, int end) {
    return ((PersistentIF)object).nextDirty(start, end);
  }
  
  @Override
  public void setDirtyFlushed(Object object, int field) {
    ((PersistentIF)object).setDirtyFlushed(field, true);
  }
  
}
