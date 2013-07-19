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
 * INTERNAL: A lookup index that extends HashMap. It never forgets
 * what it is told using put(), but will, if given a fallback object,
 * ask the fallback for values it does not have.
 */

public class HashMapIndex<K, E> extends HashMap<K, E> implements LookupIndexIF<K, E> {

  protected LookupIndexIF<K, E> fallback;

  public HashMapIndex() {
  }
  
  public HashMapIndex(Map<K, E> map) {
    super(map);
  }

  public HashMapIndex(LookupIndexIF<K, E> fallback) {
    this.fallback = fallback;
  }
  
  public HashMapIndex(Map<K, E> map, LookupIndexIF<K, E> fallback) {
    super(map);
    this.fallback = fallback;
  }
  
  public E get(Object key) {
    if (containsKey(key))
      return super.get(key);
    else if (fallback != null)
      return fallback.get((K)key);
    return null;
  }
}
