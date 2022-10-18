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

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL: Decider that maintains a cache of decisions made on a set
 * of objects. It works with any implementation of DeciderIF.</p>
 * 
 * The cache is first checked to see if a decision has already been
 * made. Otherwise a new decision is evaluated and the cache is
 * updated.</p>
 */

@Deprecated
public class CachedDecider<T> implements DeciderIF<T>, CachedIF {

  protected DeciderIF<T> decider;
  protected Map<T, Boolean> cache = new HashMap<T, Boolean>();
  
  public CachedDecider(DeciderIF<T> decider) {
    this.decider = decider;
  }

  /**
   * Gets the decider that being cached.
   */
  public DeciderIF<T> getDecider() {
    return decider;
  }
  
  /**
   * Sets the decider that is to be cached.
   */
  public void setDecider(DeciderIF<T> decider) {
    this.decider = decider;
  }
  
  @Override
  public boolean ok(T object) {
    if (object == null) return false;
    if (cache.containsKey(object)) return cache.get(object).booleanValue();
    boolean decision = decider.ok(object);
    if (decision == true)
      cache.put(object, Boolean.TRUE);
    else
      cache.put(object, Boolean.FALSE);      
    return decision;
  }
  
  @Override
  public void refresh() {
    cache.clear();
  }

}




