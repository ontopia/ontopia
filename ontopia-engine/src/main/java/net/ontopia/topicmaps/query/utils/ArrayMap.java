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

package net.ontopia.topicmaps.query.utils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL: Immutable Map implementation that stores its keys and
 * values in two arrays. Note that the implementation is immutable in
 * that you cannot modify it using the public Map interface. You can
 * modify it by modifying the underlying arrays.
 */
  
public class ArrayMap extends AbstractMap {

  protected Object[] keys;
  protected Object[] values;
  protected int size;
    
  public ArrayMap(Object[] keys) {
    setKeys(keys);
  }

  public ArrayMap(Object[] keys, Object[] values) {
    setKeys(keys);
    setValues(values);
  }

  // --- methods used to access the arrays directly

  public Object[] getKeys() {
    return keys;
  }
  
  public void setKeys(Object[] keys) {
    this.keys = keys;
    this.size = keys.length;
  }

  public Object[] getValues() {
    return values;
  }
  
  public void setValues(Object[] values) {
    this.values = values;
  }

  // --- required methods

  public Object get(Object key) {
    for (int i=0; i < size; i++)
      if (keys[i].equals(key))
        return values[i];
    return null;
  }

  public int size() {
    return size;
  }

  // --- other methods

  public boolean containsKey(Object key) {
    for (int i=0; i < size; i++)
      if (keys[i].equals(key)) return true;
    return false;
  }
    
  public boolean containsValue(Object value) {
    for (int i=0; i < size; i++)
      if (values[i].equals(value)) return true;
    return false;
  }
        
  public boolean equals(Object o) {
    if (!(o instanceof Map)) return false;
    // compare entry sets
    return entrySet().equals(((Map)o).entrySet());
  }
    
  public boolean isEmpty() {
    return size == 0;
  }
    
  public Set keySet() {
    return new HashSet(Arrays.asList(keys));
  }
    
  public Set entrySet() {
    // produce a entry set copy
    Map map = new HashMap(size);
    for (int i=0; i < size; i++)
      map.put(keys[i], values[i]);
    return map.entrySet();
  }
    
  public Collection values() {
    return Arrays.asList(values);
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public Object remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
}
