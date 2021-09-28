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
 * INTERNAL: Interface implemented by all data objects used by the
 * proxy framework.
 */

public interface PersistentIF {

  //! // NOTE: these states are similar to the ones defined in JDO.
  //! 
  //! public static final int STATE_TRANSIENT = 1;
  //! public static final int STATE_TRANSIENT_CLEAN = 2;
  //! public static final int STATE_TRANSIENT_DIRTY = 3;
  //! public static final int STATE_PERSISTENT_NEW = 4;
  //! public static final int STATE_PERSISTENT_NON_TRANSACTIONAL = 5;
  //! public static final int STATE_PERSISTENT_CLEAN = 6;
  //! public static final int STATE_PERSISTENT_DIRTY = 7;
  //! public static final int STATE_HOLLOW = 8; // Not really necessary?
  //! public static final int STATE_PERSISTENT_DELETED = 9;
  //! public static final int STATE_PERSISTENT_NEW_DELETED = 10;
  
  /**
   * INTERNAL: Returns the identity of the object.
   */
  public IdentityIF _p_getIdentity();

  /**
   * INTERNAL: Sets the identity of the object.
   */
  public void _p_setIdentity(IdentityIF identity);

  /**
   * INTERNAL: Returns the transaction that is responible for managing
   * the object.
   */
  public TransactionIF _p_getTransaction();

  /**
   * INTERNAL: Sets the transaction that is responible for managing
   * the object.
   */
  public void _p_setTransaction(TransactionIF transaction);
  
  /**
   * INTERNAL: Returns the object.type. This information is used by
   * the transaction to handle the persistent mapping for the object.
   */
  public Class<?> _p_getType();

  public int _p_getFieldCount();

  // -----------------------------------------------------------------------------
  // State
  // -----------------------------------------------------------------------------

  public boolean isTransient();

  public boolean isNewObject();
  
  public void setNewObject(boolean newobject);

  public boolean isInDatabase();

  public void setInDatabase(boolean inDatabase);

  public boolean isPersistent();

  public void setPersistent(boolean persistent);

  public boolean isDeleted();

  public void setDeleted(boolean deleted);

  // -----------------------------------------------------------------------------
  // Data access
  // -----------------------------------------------------------------------------

  public boolean isLoaded(int field);

  public Object loadValue(FieldInfoIF finfo);

  // -----------------------------------------------------------------------------
  // Dirty
  // -----------------------------------------------------------------------------

  public boolean isDirty();

  public boolean isDirty(int field);

  public int nextDirty(int start);

  public int nextDirty(int start, int end);
  
  public void setDirtyFlushed(int field, boolean dirty);

  // -----------------------------------------------------------------------------
  // Reset
  // -----------------------------------------------------------------------------

  public void clearAll();

  // -----------------------------------------------------------------------------
  // Detach
  // -----------------------------------------------------------------------------

  // NOTE: metod is called when object is deleted from data store
  public void detach();

}






