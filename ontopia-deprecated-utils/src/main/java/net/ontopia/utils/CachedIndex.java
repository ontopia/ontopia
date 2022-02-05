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
 * INTERNAL: A LookupIndexIF which uses another, slower, LookupIndexIF
 * as a fallback and caches the values attached to the most commonly
 * requested keys using an LRU strategy. There is a maximum number of
 * keys that can be stored in the index and the index will
 * automatically prune the less-used keys to avoid the index growing
 * above this maximum size.
 */

@Deprecated
public class CachedIndex<K, E> implements LookupIndexIF<K, E> {
  private LookupIndexIF<K, E> fallback;
  private int           max;       // max number of entries in cache
  private int           entries;   // current number of entries in cache
  private int           decay;     // how many hits to decay hit count by
                                   // when pruning. (those with less removed.)
  private Entry[]       data;      // current entries
  private double        threshold; // will rehash when entries/data.len>thresh

  private boolean       nulls;     // store nulls retrieved from fallback.

  // statistics collection
  private long          lookups;
  private long          hits;
  private long          rehashes;
  private long          prunings;
  
  /**
   * Creates an index with the given fallback and default settings.
   */

  public CachedIndex(LookupIndexIF<K, E> fallback) {
    this.fallback  = fallback;
    this.max       = 10000;
    this.data      = new Entry[1001];
    this.entries   = 0;
    this.threshold = 0.25;
    this.decay     = 10;
    this.nulls     = true;
  }
  
  /**
   * Creates an index with the given fallback, default settings and
   * the specified nulls setting.
   */

  public CachedIndex(LookupIndexIF<K, E> fallback, boolean nulls) {
    this(fallback);
    this.nulls = nulls;
  }

  /**
   * Creates an index with the given fallback and settings.
   * @param fallback The index to ask if the value is not found in the cache.
   * @param max The max number of keys to store in the cache (default: 10000).
   * @param size The initial size of the cache.
   * @param nulls Store null values retrieved from fallback.
   */

  public CachedIndex(LookupIndexIF<K, E> fallback, int max, int size, boolean nulls) {
    this.fallback  = fallback;
    this.max       = max;
    this.data      = new Entry[size];
    this.entries   = 0;
    this.threshold = 0.25;
    this.decay     = 10;
    this.nulls     = nulls;
  }
  
  @Override
  public E get(K key) {
    Entry entry = data[(key.hashCode() & 0x7FFFFFFF) % data.length];

    while (entry != null && !entry.key.equals(key))
      entry = entry.next;

    lookups++;
    if (entry == null) { // not found
      E result = fallback.get(key);
      if (result == null && !nulls) return null; // do not store null values
      entry = addEntry(new Entry(key, result));
    } else {
      hits++;
      entry.hits++;
    }

    return (E) entry.value;
  }

  @Override
  public E put(K key, E value) {
    // check if key already there; otherwise may end up with two entries
    // with same key
    Entry entry = data[(key.hashCode() & 0x7FFFFFFF) % data.length];
    while (entry != null && !entry.key.equals(key))
      entry = entry.next;

    if (entry == null)
      addEntry(new Entry(key, value));
    else
      entry.value = value;

    return value;
  }

  @Override
  public E remove(K key) {
    int ix = (key.hashCode() & 0x7FFFFFFF) % data.length;
    Entry<K, E> entry = data[ix];
    Entry<K, E> previous = null;

    while (entry != null) {
      if (entry.key.equals(key)) {
        // FIXME: pass on news to fallback?
        if (previous == null)
          data[ix] = entry.next;
        else
          previous.next = entry.next;
        
        entries--;
        return (E)entry.value;
      }

      previous = entry;
      entry = entry.next;
    }    
    return null;
  }
  
  // --- Extra methods

  public int getKeyNumber() {
    return entries;
  }

  public void writeReport() {
    System.out.println("--- CachedIndex report");
    System.out.println("lookups:    " + lookups);
    System.out.println("misses:     " + (lookups - hits));
    System.out.println("ratio:      " + (((float) hits) / lookups));
    System.out.println("array size: " + data.length);
    System.out.println("keys:       " + entries);
    System.out.println("rehashes:   " + rehashes);
    System.out.println("prunings:   " + prunings);
  }
  
  // --- Internal methods

  /**
   * Called to add an entry object into the data array. Assumes that
   * no entry with the same key already exists in the data array.
   */
  
  private Entry<K, E> addEntry(Entry<K, E> newEntry) {
    if (entries >= max) 
      prune();
    else if (((float) entries) / data.length > threshold)
      rehash(data.length*2 + 1);

    int ix = (newEntry.key.hashCode() & 0x7FFFFFFF) % data.length;
    if (data[ix] == null)
      data[ix] = newEntry;
    else {
      newEntry.next = data[ix];
      data[ix] = newEntry;
    }

    entries++;
    return newEntry;
  }

  /**
   * Removes some of the keys in the cache, keeping only the most
   * frequently requested keys.
   */
  
  protected void prune() {
    prunings++;
//      System.out.println("PRUNING! Keys now: " + entries);
    for (int ix = 0; ix < data.length; ix++) {
      Entry current = data[ix];
      Entry previous = null;

      while (current != null) {
        if (current.hits < decay) {
          if (previous == null)
            data[ix] = current.next;
          else
            previous.next = current.next;
          entries--;
        } else
          current.hits -= decay;
        
        current = current.next;
      }
    }
//      System.out.println("Done. Keys now: " + entries);
  }

  /**
   * Increases the size of the data array to the given size.
   */
  
  private void rehash(int size) {
    rehashes++;
//      System.out.println("rehashing to: " + size + "  keys: " + entries);
    Entry[] olddata = data;
    data = new Entry[size];

    for (int ix = 0; ix < olddata.length; ix++) {
      Entry current = olddata[ix];

     while (current != null) {
        Entry next = current.next;
        current.next = null;

        // copied from addEntry
        int pos = (current.key.hashCode() & 0x7FFFFFFF) % data.length;
        if (data[pos] == null)
          data[pos] = current;
        else {
          current.next = data[pos];
          data[pos] = current;
        }
        // end of copy
        
        current = next; 
      }
    }
  }
  
  // --- Internal Entry class

  public class Entry<A, B> {
    public Object value;
    public Object key;
    public int    hits;
    public Entry<A, B>  next;

    public Entry(A key, B value) {
      this.key = key;
      this.value = value;
      this.hits = 1;
    }
  }
  
}




