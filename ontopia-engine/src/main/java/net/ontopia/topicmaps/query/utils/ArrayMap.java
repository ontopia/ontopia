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
  
public class ArrayMap<K, V> extends AbstractMap<K, V> {

  protected K[] keys;
  protected V[] values;
  protected int size;
    
  public ArrayMap(K[] keys) {
    setKeys(keys);
  }

  public ArrayMap(K[] keys, V[] values) {
    setKeys(keys);
    setValues(values);
  }

  // --- methods used to access the arrays directly

  public Object[] getKeys() {
    return keys;
  }
  
  public void setKeys(K[] keys) {
    this.keys = keys;
    this.size = keys.length;
  }

  public V[] getValues() {
    return values;
  }
  
  public void setValues(V[] values) {
    this.values = values;
  }

  // --- required methods

  @Override
  public V get(Object key) {
    for (int i=0; i < size; i++) {
      if (keys[i].equals(key)) {
        return values[i];
      }
    }
    return null;
  }

  @Override
  public int size() {
    return size;
  }

  // --- other methods

  @Override
  public boolean containsKey(Object key) {
    for (int i=0; i < size; i++) {
      if (keys[i].equals(key)) {
        return true;
      }
    }
    return false;
  }
    
  @Override
  public boolean containsValue(Object value) {
    for (int i=0; i < size; i++) {
      if (values[i].equals(value)) {
        return true;
      }
    }
    return false;
  }
        
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Map)) {
      return false;
    }
    // compare entry sets
    return entrySet().equals(((Map)o).entrySet());
  }
    
  @Override
  public boolean isEmpty() {
    return size == 0;
  }
    
  @Override
  public Set<K> keySet() {
    return new HashSet<K>(Arrays.asList(keys));
  }
    
  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    // produce a entry set copy
    Map<K, V> map = new HashMap<K, V>(size);
    for (int i=0; i < size; i++) {
      map.put(keys[i], values[i]);
    }
    return map.entrySet();
  }
    
  @Override
  public Collection<V> values() {
    return Arrays.asList(values);
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
}
