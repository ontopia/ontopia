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

import java.util.Map;
import java.util.WeakHashMap;

/**
 * INTERNAL: A lookup index that extends WeakHashMap. It forgets what
 * it is told using put() at the will of the garbage collector, but
 * will, if given a fallback object, ask the fallback for values it
 * does not have.
 */

public class WeakHashMapIndex extends WeakHashMap implements LookupIndexIF {

  protected LookupIndexIF fallback;

  public WeakHashMapIndex() {
  }
  
  public WeakHashMapIndex(Map map) {
    putAll(map);
  }

  public WeakHashMapIndex(LookupIndexIF fallback) {
    this.fallback = fallback;
  }
  
  public WeakHashMapIndex(Map map, LookupIndexIF fallback) {
    putAll(map);
    this.fallback = fallback;
  }
  
  public Object get(Object key) {
    if (containsKey(key))
      return super.get(key);
    else if (fallback != null)
      return fallback.get(key);
    else
      return null;
  }
}




