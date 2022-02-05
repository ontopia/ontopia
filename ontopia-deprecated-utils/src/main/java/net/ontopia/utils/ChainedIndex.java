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

/**
 * INTERNAL: A lookup index that delegates LookupIndexIF calls to
 * chains of LookupIndexIFs.<p>
 *
 * get(key) returns the first non-null value returned from a getter.<p>
 * put(key) removes the key from all putters.<p>
 * remove(key) removes the key from all removers.<p>
 *
 * @since 1.3.2
 */
@Deprecated
public class ChainedIndex<K, E> implements LookupIndexIF<K, E> {

  protected LookupIndexIF<K, E>[] getters;
  protected LookupIndexIF<K, E>[] putters;
  protected LookupIndexIF<K, E>[] removers;
  protected int gsize;
  protected int psize;
  protected int rsize;
  protected E missvalue = null;
  
  public ChainedIndex(LookupIndexIF<K, E>[] chain) {
    this(chain, chain, chain);
  }
  
  public ChainedIndex(LookupIndexIF<K, E>[] getters, LookupIndexIF<K, E>[] setters) {
    this(getters, setters, setters);
  }
  
  @SuppressWarnings({"rawtypes", "unchecked"})
  public ChainedIndex(LookupIndexIF<K, E> getter, LookupIndexIF<K, E>[] setters) {
    this(new LookupIndexIF[] { getter }, setters);
  }
  
  @SuppressWarnings({"rawtypes", "unchecked"})
  public ChainedIndex(LookupIndexIF<K, E>[] getters, LookupIndexIF<K, E> setter) {
    this(getters, new LookupIndexIF[] { setter });
  }
  
  public ChainedIndex(LookupIndexIF<K, E>[] getters, LookupIndexIF<K, E>[] putters, LookupIndexIF<K, E>[] removers) {
    this.getters = getters;
    this.putters = putters;
    this.removers = removers;
    this.gsize = getters.length;
    this.psize = putters.length;
    this.rsize = removers.length;
  }
  
  @Override
  public E get(K key) {
    // Return result of first non-null get(key) call.
    for (int i=0; i < gsize; i++) {
      E value = getters[i].get(key);
      if (value == missvalue) continue;
      return value;
    }
    return missvalue;
  }

  /**
   * INTERNAL: Gets the missvalue member, which is used to decide
   * whether an index lookup missed or not. The default is null.
   *
   * @since 1.3.4
   */
  public Object getMissValue() {
    return missvalue;
  }

  /**
   * INTERNAL: Sets the missvalue member, which is used to decide
   * whether an index lookup missed or not.
   *
   * @since 1.3.4
   */
  public void setMissValue(E missvalue) {
    this.missvalue = missvalue;
  }
  
  @Override
  public E put(K key, E value) {
    // Call put(key) on all putters
    E rval = null;
    for (int i=0; i < psize; i++) {
      rval = putters[i].put(key, value);
    }
    return rval;
  }
  
  @Override
  public E remove(K key) {
    // Call remove(key) on all removers
    E rval = null;
    for (int i=0; i < rsize; i++) {
      rval = removers[i].remove(key);
    }
    return rval;
  }
  
}
