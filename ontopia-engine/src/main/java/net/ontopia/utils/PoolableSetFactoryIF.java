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
 * INTERNAL: Factory interface used by SetPoolIF to create new
 * PoolableSetIF instances.
 *
 * @since 2.0
 */

public interface PoolableSetFactoryIF {

  /**
   * INTERNAL: Returns a new empty poolable set.
   */
  public PoolableSetIF createSet();

  /**
   * INTERNAL: Returns a new poolable set that contains the given
   * elements.
   */
  public PoolableSetIF createSet(Set set);
  
  /**
   * INTERNAL: Returns a new poolable set that contains the given
   * elements plus the single object.
   */
  public PoolableSetIF createSetAdd(Set set, Object o);
  
  /**
   * INTERNAL: Returns a new poolable set that contains the given
   * elements minus the single object.
   */
  public PoolableSetIF createSetRemove(Set set, Object o);
  
}
