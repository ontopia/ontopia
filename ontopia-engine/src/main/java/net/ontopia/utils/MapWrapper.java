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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL: Abstract Map implementation that delegates all its method
 * calls to the other map instance. Class is intended to make it
 * easier to implement map instances that is intended to just override
 * a few methods on other map implementations without knowing the
 * exact type of the other map instances.
 *
 * @since 2.0
 */
public abstract class MapWrapper implements Map {

  protected Map other;

  public MapWrapper(Map other) {
    this.other = other;
  }
  
  public int size() {
    return other.size();
  }

  public boolean isEmpty() {
    return other.isEmpty();
  }
  
  public boolean containsValue(Object value) {
    return other.containsValue(value);
  }

  public boolean containsKey(Object key) {
    return other.containsKey(key);
  }

  public Object get(Object key) {
    return other.get(key);
  }

  public Object put(Object key, Object value) {
    return other.put(key, value);
  }

  public void putAll(Map t) {
    other.putAll(t);
  }

  public Object remove(Object key) {
    return other.remove(key);
  }

  public void clear() {
    other.clear();
  }

  public Set keySet() {
    return other.keySet();
  }

  public Collection values() {
    return other.values();
  }

  public Set entrySet() {
    return other.entrySet();
  }

  public boolean equals(Object o) {
    return other.equals(o);
  }

  public int hashCode() {
    return other.hashCode();
  }

  public String toString() {
    return other.toString();
  }
  
}
