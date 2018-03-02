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

/**
 * INTERNAL: Interface for reading and updating object field
 * values. The actual object field accessed by the implementation is
 * not exposed by the interface.
 */

public interface FieldAccessIF {

  /**
   * INTERNAL: Loads the field value for the given object
   * identity. The specified access registrar will be notified about
   * the value(s) read from the database.
   *
   * @return The value loaded for the specific field. Note that if the
   * field is a reference field the identity will be returned, not the
   * actual object. This is because the storage system does not deal
   * with persistent object instances directly.
   *
   * @throws IdentityNotFoundException if the identity was not found.
   */
  Object load(AccessRegistrarIF registrar, IdentityIF identity) throws Exception;

  /**
   * INTERNAL: Loads the field value for all the given object
   * identities. The specified access registrar will be notified about
   * the value(s) read from the database.
   *
   * @return The value loaded for the specific field for the current
   * identity. Note that if the field is a reference field the
   * identity will be returned, not the actual object. This is because
   * the storage system does not deal with persistent object instances
   * directly.
   *
   * @throws IdentityNotFoundException if the identity was not found.
   */
  Object loadMultiple(AccessRegistrarIF registrar, Collection<IdentityIF> identities, 
			     IdentityIF current) throws Exception;

  /**
   * INTERNAL: The object field is dirty and a call to this method
   * should cause the field value to be updated. Note that the field
   * access may also store other field values if it decides to do
   * so. After the field value(s) has been updated the dirty flag(s)
   * should be set to false.
   */
  void storeDirty(ObjectAccessIF oaccess, Object object) throws Exception;

  //! /**
  //!  * INTERNAL: Sets the field value for the given object
  //!  * identity. This method is only applicable for 1:1 fields.
  //!  */
  //! public void set(IdentityIF identity, Object value) throws Exception;
  //! 
  //! /**
  //!  * INTERNAL: Adds the given values to the field value for the given
  //!  * object identity. This method is only applicable for 1:M and M:M
  //!  * fields.
  //!  */
  //! public void add(IdentityIF identity, Collection values) throws Exception;
  //! 
  //! /**
  //!  * INTERNAL: Removes the given values from the field value for the
  //!  * given object identity. This method is only applicable for 1:M and
  //!  * M:M fields.
  //!  */
  //! public void remove(IdentityIF identity, Collection values) throws Exception;
  //! 
  //! //! TODO: Map support
  //! //! public void put(IdentityIF identity, Object key, Object value) throws Exception;
  //! //! public void remove(IdentityIF identity, Object key) throws Exception;
  
  /**
   * INTERNAL: Clears the field value for the given object
   * identity. This method is only applicable for 1:M and M:M fields.
   */
  void clear(IdentityIF identity) throws Exception;
  
}
