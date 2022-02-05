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

package net.ontopia.topicmaps.impl.utils;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import net.ontopia.utils.LookupIndexIF;

/**
 * INTERNAL: A lookup index that transforms its entry values to and
 * from SoftReferences. Storage of the actual value is delegated to
 * the nested index.
 */

@Deprecated
public class StatisticsIndex<K, V> implements LookupIndexIF<K, V> {

  protected int total;
  protected int hits;
  protected int misses;
  protected int misses_deref;

  protected String name;
  protected LookupIndexIF<K, Reference<V>> index;

  public StatisticsIndex(String name, LookupIndexIF<K, Reference<V>> index) {
    this.name = name;
    this.index = index;
  }
  
  @Override
  public V get(K key) {
    total++;

    Object retval = index.get(key);
    if (retval == null) 
      misses++;
    else {
      retval = ((Reference<V>)retval).get();
      if (retval == null)
	misses_deref++;
    }
    
    if (retval != null) hits++;

    if (total % 1000 == 0) dump();
    return (V)retval;
  }

  @Override
  public V put(K key, V value) {
    Reference<V> retval = index.put(key, new SoftReference<V>(value));
    if (retval == null)
      return null;
    else
      return retval.get();
  }

  @Override
  public V remove(K key) {
    Reference<V> retval = index.remove(key);
    if (retval == null)
      return null;
    else
      return retval.get();
  }

  protected int percent(int c, int total) {
    if (c == 0) return 0;
    return Math.round(((100.0f*c)/(1.0f*total)));
  }

  public void dump() {
    System.out.println("StatisticsIndex: " + name);
    System.out.println("  hits: " + hits + " (" + percent(hits, total) + "%)");
    System.out.println("  misses: " + misses + " (" + percent(misses, total) + "%)");
    System.out.println("  misses deref: " + misses_deref + " (" + percent(misses_deref, total) + "%)");
  }

}





