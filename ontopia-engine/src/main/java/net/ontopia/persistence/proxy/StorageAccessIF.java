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
import net.ontopia.persistence.query.jdo.JDOQuery;
  
/**
 * INTERNAL: Interface that encapsulates the access to the actual data
 * repository.
 */

public interface StorageAccessIF {

  /**
   * INTERNAL: Gets the storage access id. This id is unique for a
   * given StorageIF instance.
   */
  public String getId();

  /**
   * INTERNAL: Returns the storage definition that the access uses.
   */
  public StorageIF getStorage();

  /**
   * INTERNAL: Returns true if the storage access is read-only.
   */
  public boolean isReadOnly();

  /**
   * INTERNAL: Gets the value of the specified property.
   */
  public String getProperty(String property);
  
  // -----------------------------------------------------------------------------
  // StorageAccessIF (internal)
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Returns true if the storage access is valid.
   */
  public boolean validate();
  
  /**
   * INTERNAL: Commits the changes performed in the transaction.
   */
  public void commit();
  
  /**
   * INTERNAL: Aborts all changes performed in the transaction.
   */
  public void abort();

  /**
   * INTERNAL: Closes the storage access, which allows it to free its
   * resources.
   */
  public void close();

  /**
   * INTERNAL: Called when the transaction requires the transaction
   * changes to be stored by the storage access (i.e. written to the
   * database).<p>
   *
   * This method exists mainly to allow storage access implementations
   * to optimize its communication with data repositories. An example
   * of this is writing transaction changes in batches to improve
   * performance.<p>
   *
   * Note that the transaction will always call this method at the end
   * of its store method. It will do this so that it is sure that the
   * changes will be visible inside the data repository.<p>
   */
  public void flush();

  // -----------------------------------------------------------------------------
  // Data storage access and modifications
  // -----------------------------------------------------------------------------
  
  /**
   * INTERNAL: Check for the existence of the object identity in the
   * data repository. An exception will be thrown if the object does
   * not exist. If it exists the access registrar will be notified.
   *
   * @return true if object was found in the data store, false
   * otherwise.
   */
  public boolean loadObject(AccessRegistrarIF registrar, IdentityIF identity);
  
  /**
   * INTERNAL: Requests the loading of the specified field for the
   * given object identity. An exception will be thrown if the object
   * does not exist. If it exists the access registrar will be
   * notified.
   *
   * @return The value loaded for the specific field. Note that if the
   * field is a reference field the identity will be returned, not the
   * actual object. This is because the storage system does not deal
   * with persistent object instances directly.
   *
   * @throws IdentityNotFoundException if the identity was not found.
   */
  public Object loadField(AccessRegistrarIF registrar, IdentityIF identity, int field)
    throws IdentityNotFoundException;
  
  /**
   * INTERNAL: Requests the loading of the specified field for all the
   * given object identities. An exception will be thrown if the
   * current object does not exist. If it exists the access registrar
   * will be notified.
   *
   * @return The value loaded for the specific field. Note that if the
   * field is a reference field the identity will be returned, not the
   * actual object. This is because the storage system does not deal
   * with persistent object instances directly.
   *
   * @throws IdentityNotFoundException if the identity was not found.
   */
  public Object loadFieldMultiple(AccessRegistrarIF registrar, Collection identities, 
                                 IdentityIF current, Class<?> type, int field)
    throws IdentityNotFoundException;

  /**
   * INTERNAL: Called by the transaction when it requests the new
   * object to be created in the data repository. The ObjectAccessIF
   * object is used to access information about the object as needed.
   */
  public void createObject(ObjectAccessIF oaccess, Object object);

  //! public void createObject(IdentityIF);
  
  /**
   * INTERNAL: Called by the transaction when it requests the
   * object to be deleted in the data repository.
   */
  public void deleteObject(ObjectAccessIF oaccess, Object object);

  //! public void deleteObject(ObjectAccessIF oaccess, Object object);
  
  /**
   * INTERNAL: Stores object fields that are dirty in the database.
   */
  public void storeDirty(ObjectAccessIF oaccess, Object object);
  
  //! /**
  //!  * INTERNAL: Called by the transaction when it requests the object
  //!  * field value to be set in the data repository. This method is only
  //!  * applicable for 1:M and M:M fields.
  //!  */
  //! public void setFieldValue(IdentityIF identity, int field, Object value);
  //! 
  //! /**
  //!  * INTERNAL: Called by the transaction when it requests the given
  //!  * values to be added to the object field in the data
  //!  * repository. This method is only applicable for 1:M and M:M
  //!  * fields.
  //!  */
  //! public void addFieldValues(IdentityIF identity, int field, Collection values);
  //! 
  //! /**
  //!  * INTERNAL: Called by the transaction when it requests the given
  //!  * values to be removed from the object field in the data
  //!  * repository. This method is only applicable for 1:M and M:M
  //!  * fields.
  //!  */
  //! public void removeFieldValues(IdentityIF identity, int field, Collection values);
  //! 
  //! /**
  //!  * INTERNAL: Called by the transaction when it requests the given
  //!  * object field to be cleared in the data repository. This method is
  //!  * only applicable for 1:M and M:M fields.
  //!  */
  //! public void clearField(IdentityIF identity, int field);
  
  // -----------------------------------------------------------------------------
  // Identity generator
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Called by the application when it requests a new object
   * identity for a given object type.
   */
  public IdentityIF generateIdentity(Class<?> type);

  // -----------------------------------------------------------------------------
  // Queries
  // -----------------------------------------------------------------------------

  // TODO: Move all of these into StorageAccessIF and get rid of TransactionIF.
  
  /**
   * INTERNAL: Creates a query instance for the given transaction.
   */
  public QueryIF createQuery(String name, ObjectAccessIF oaccess, AccessRegistrarIF registrar);
  
  /**
   * INTERNAL: Build a QueryIF from the specified JDO query instance.
   */
  public QueryIF createQuery(JDOQuery jdoquery, ObjectAccessIF oaccess, AccessRegistrarIF registrar, boolean lookup_identities);
  
}






