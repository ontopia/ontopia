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
 * INTERNAL: Interface used to represent data store object
 * identity. The identity has two parts; the type of object and an
 * ordered list of primary key components. Note that an identity
 * instance should always be immutable.
 * 
 * Warning: Implementations of this class must all have the same 
 * hashCode() behaviour.
 */

public interface IdentityIF extends Cloneable {

  /**
   * INTERNAL: Returns the type of object. The returned value
   * indicates the classification of the identified object. See also
   * {@link PersistentIF#_p_getType()}.
   */
  public Class<?> getType();

  /**
   * INTERNAL: Returns the number of primary key components that the
   * identity has.
   */
  public int getWidth();

  /**
   * INTERNAL: Returns the primary key component with the specified index.
   */
  public Object getKey(int index);

  /**
   * INTERNAL: Creates an object instance of the type defined by this
   * identity.
   */
  public Object createInstance() throws Exception;
  // FIXME: Consider moving this method elsewhere.

  public Object clone();

}
