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

package net.ontopia.utils;

import java.util.Set;

/**
 * INTERNAL: Interface implemented by sets that can be pooled by a
 * SetPoolIF. This interface is used byte SetPoolIFs to manage the
 * life cycle of the sets that they manage.
 *
 * @since 2.0
 */
public interface PoolableSetIF extends Set {

  /**
   * INTERNAL: Return the current reference count. The reference count
   * is used to control the life cycle of the pooled set. The pool
   * instance that manages this set will use the reference count to
   * figure out when to drop the set from its pool.
   */
  public int getReferenceCount();
  
  /**
   * INTERNAL: Increment and return reference count.
   */
  public int referenced(SetPoolIF pool); // +1 and return reference count
  
  /**
   * INTERNAL: Decrement and return reference count.
   */
  public int dereferenced(SetPoolIF pool); // -1 and return reference count

  /**
   * INTERNAL: Returns true if the given set will be equal to this set
   * if the given object had been added to this set.
   */
  public boolean equalsAdd(Set set, Object add);

  /**
   * INTERNAL: Returns true if the given set will be equal to this set
   * if the given object had been removed from this set.
   */
  public boolean equalsRemove(Set set, Object remove);
  
}
