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
 * INTERNAL: Interface implemented by all set pools.
 *
 * @since 2.0
 */
public interface SetPoolIF extends Set {
  
  /**
   * INTERNAL: Increment reference count for the given set.
   */
  public Set reference(Set set);

  /**
   * INTERNAL: Decrement reference count for the given set.
   */
  public void dereference(Set pooled);

  /**
   * INTERNAL: Return a set that is the result of adding the given
   * object from the given set. If the dereference flag is set the set
   * reference count of the old set will be decremented by one.
   */
  public Set add(Set set, Object added, boolean dereference);

  /**
   * INTERNAL: Return a set that is the result of removing the given
   * object from the given set. If the dereference flag is set the set
   * reference count of the old set will be decremented by one.
   */
  public Set remove(Set set, Object removed, boolean dereference);
  
}
