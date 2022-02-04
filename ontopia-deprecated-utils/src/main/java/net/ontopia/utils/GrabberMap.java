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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL: An implementation of Map that uses a key grabber and a
 * value grabber to extract its content.
 */

@Deprecated
public class GrabberMap<O, KG, VG> implements Map<KG, Collection<VG>>, CachedIF {

  protected Collection<O> coll;
  protected GrabberIF<O, KG> key_grabber;
  protected GrabberIF<O, VG> value_grabber;

  protected Map<KG, Collection<VG>> grabbed_map;
  protected boolean grabbed;
  
  public GrabberMap(Collection<O> coll, GrabberIF<O, KG> key_grabber, GrabberIF<O, VG> value_grabber) {
    this.coll = coll;
    this.key_grabber = key_grabber;
    this.value_grabber = value_grabber;
  }

  public Map<KG, Collection<VG>> getNestedMap() {
    return getMap();
  }
  
  public void setNestedMap(Map<KG, Collection<VG>> map) {
    this.grabbed_map = map;    
  }
  
  /**
   * Refreshes the map by looping over the nested collection
   * and regrabbing keys and values. Any changes done to the previous
   * collection will be lost.
   */
  @Override
  public void refresh() {
    if (grabbed_map == null)
      grabbed_map = new HashMap<KG, Collection<VG>>();
    else
      grabbed_map.clear();
    
    // Loop over all objects in the collection
    Iterator<O> iter = coll.iterator();
    while (iter.hasNext()) {
      O object = iter.next();
      KG grabbed_key = key_grabber.grab(object);
      VG grabbed_value = value_grabber.grab(object);
      if (!grabbed_map.containsKey(grabbed_key)) grabbed_map.put(grabbed_key, new HashSet<VG>());
      grabbed_map.get(grabbed_key).add(grabbed_value);
    }
    grabbed = true;
  }

  protected Map<KG, Collection<VG>> getMap() {
    if (grabbed) return grabbed_map;
    refresh();
    return grabbed_map;
  }

  @Override
  public void clear() {
    getMap().clear();
  }

  @Override
  public boolean containsKey(Object key) {
    return getMap().containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return getMap().containsValue(value);
  }

  @Override
  public Set entrySet() {
    return getMap().entrySet();
  }
  

  @Override
  public boolean equals(Object o) {
    return getMap().equals(o);
  }

  @Override
  public Collection<VG> get(Object key) {
    return getMap().get(key);
  }

  @Override
  public int hashCode() {
    return getMap().hashCode();
  }

  @Override
  public boolean isEmpty() {
    return getMap().isEmpty();
  }

  @Override
  public Set keySet() {
    return getMap().keySet();
  }

  @Override
  public Collection<VG> put(KG key, Collection<VG> value) {
    return getMap().put(key, value);
  }

  @Override
  public void putAll(Map t) {
    getMap().putAll(t);
  }

  @Override
  public Collection<VG> remove(Object key) {
    return getMap().remove(key);
  }

  @Override
  public int size() {
    return getMap().size();
  }

  @Override
  public Collection values() {
    return getMap().values();
  }
   
}




