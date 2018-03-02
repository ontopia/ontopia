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
 * INTERNAL: Interface for accessing class instances in the
 * database. This include loading, creating, and deleting objects.
 */

public interface ClassAccessIF {

  /**
   * INTERNAL: Loads the object identity from the database.
   *
   * @return true if object was found in the data store, false
   * otherwise.
   */
  boolean load(AccessRegistrarIF registrar, IdentityIF identity) throws Exception;

  /**
   * INTERNAL: Loads the specified object field for the given identity
   * from the database.
   */
  Object loadField(AccessRegistrarIF registrar, IdentityIF identity, int field) throws Exception;

  /**
   * INTERNAL: Loads the specified object field for the given
   * identitys from the database.
   */
  Object loadFieldMultiple(AccessRegistrarIF registrar, Collection<IdentityIF> identities, 
				  IdentityIF current, int field) throws Exception;
  
  /**
   * INTERNAL: Creates the new object in the database. Note that the
   * object identity can be extracted from the object using the
   * supplied object access instance.
   */
  void create(ObjectAccessIF oaccess, Object object) throws Exception;

  /**
   * INTERNAL: Deletes the object identity from the database.
   */
  void delete(ObjectAccessIF oaccess, Object object) throws Exception;

  /**
   * INTERNAL: Stores object fields that are dirty in the
   * database. Note that the object identity can be extracted from the
   * object using the supplied object access instance.
   */
  void storeDirty(ObjectAccessIF oaccess, Object object) throws Exception;

}






