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

import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;

/**
 * INTERNAL: A lookup index implementation that uses soft references
 * for the keys, in order to allow them to be garbage-collected. It
 * doesn't support a fallback, since this is not needed where it is
 * used. It's simply a cache that allows its keys to be GC-ed. Note
 * that the keys passed in should not be SoftReferences; the index
 * will wrap them as references where needed.
 *
 * <p>General approach is closed hashing, like in CompactHashSet.
 * We simplify a little by assuming that null keys do not occur in
 * put() and remove().
 */
public class SoftHashMapIndex<K, E> implements LookupIndexIF<K, E> {
  private ReferenceQueue queue;
  public SoftEntry<K, E>[] entries;
  private int freecells; // number of free cells in entries array
  private int elements;  // number of elements in the index
  private int operations; // used to trigger processQueue every MAX_OPS

  private final static SoftEntry DELETED = new SoftEntry();
  private final static int INITIAL_SIZE = 101; // largish prime
  private final static double LOAD_FACTOR = 0.67; // trading memory for speed
  private final static int MAX_OPS = 10;

  public SoftHashMapIndex() {
    queue = new ReferenceQueue();
    entries = new SoftEntry[INITIAL_SIZE];
    freecells = INITIAL_SIZE;
    elements = 0;
  }

  public E get(K key) {
    if (++operations % MAX_OPS == 0)
      processQueue();
    
    // put never receives a null key
    if (key == null)
      return null;

    // get on with it
    int hash = key.hashCode();
    int index = (hash & 0x7FFFFFFF) % entries.length;
    int offset = 1;

    // search for entry (while !null and !this)
    while(entries[index] != null &&
          !(entries[index].keyhash == hash &&
            entries[index].equals(key))) {
      index = ((index + offset) & 0x7FFFFFFF) % entries.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }

    // entry has value, too
    if (entries[index] != null)
      return entries[index].value;
    else
      return null;
  }

  // same as get(Object), but this method returns the key object. the
  // method is used by the rdbms o/r mapper for interning of identity
  // objects.
  public K getKey(K key) {
    if (++operations % MAX_OPS == 0)
      processQueue();
    
    // put never receives a null key
    if (key == null)
      return null;

    // get on with it
    int hash = key.hashCode();
    int index = (hash & 0x7FFFFFFF) % entries.length;
    int offset = 1;

    // search for entry (while !null and !this)
    while(entries[index] != null &&
          !(entries[index].keyhash == hash &&
            entries[index].equals(key))) {
      index = ((index + offset) & 0x7FFFFFFF) % entries.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }

    // entry has value, too
    if (entries[index] != null)
      return entries[index].get();
    else
      return null;
  }

  // doesn't support null!
  public E put(K key, E value) {
    if (++operations % MAX_OPS == 0)
      processQueue();
    
    int hash = key.hashCode();
    int index = (hash & 0x7FFFFFFF) % entries.length;
    int offset = 1;
    int deletedix = -1; // deleted object we can overwrite (if not already present)
    
    // search for the object (continue while !null and !this object)
    while(entries[index] != null &&
          !(entries[index].keyhash == hash &&
            entries[index].equals(key))) {

      // if there's a deleted object here we can put this object here,
      // provided it's not in here somewhere else already
      if (entries[index] == DELETED)
        deletedix = index;
      
      index = ((index + offset) & 0x7FFFFFFF) % entries.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }
    
    if (entries[index] == null) { // wasn't present already
      if (deletedix != -1) // reusing a deleted cell
        index = deletedix;
      else
        freecells--;

      elements++;
      entries[index] = new SoftEntry(key, value, queue);
      // rehash with same capacity
      if (1 - (freecells / (double) entries.length) > LOAD_FACTOR)
        rehash();
      return null;
    } else { // was there already 
      E oldvalue = entries[index].value;
      entries[index].value = value;
      return oldvalue;
    }
  }

  // doesn't support null!
  public E remove(K key) {
    if (++operations % MAX_OPS == 0)
      processQueue();
    
    int hash = key.hashCode();
    int index = (hash & 0x7FFFFFFF) % entries.length;
    int offset = 1;
    
    // search for the object (continue while !null and !this object)
    while(entries[index] != null &&
          !(entries[index].keyhash == hash &&
            entries[index].equals(key))) {
      index = ((index + offset) & 0x7FFFFFFF) % entries.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }

    // we found the right position, now do the removal
    if (entries[index] != null) {
      // we found the object
      elements --;
      E value = entries[index].value;
      entries[index] = DELETED;
      return value;
    } else
      return null;
  }

  // --- internal methods

  private void processQueue() {
    Object sk;
    while ((sk = queue.poll()) != null) {
      removeSoftEntry((SoftEntry) sk);
    }
    operations = 0;
  }

  private void removeSoftEntry(SoftEntry<K, E> entry) {
    // in this case the key in the entry will have gone to null
    // so we have to use entry identity to find the right object
    
    int hash = entry.keyhash;
    int index = (hash & 0x7FFFFFFF) % entries.length;
    int offset = 1;
    
    // search for the object (continue while !null and !this object)
    while(entries[index] != null &&
          !(entries[index].keyhash == hash &&
            entries[index] == entry)) {
      index = ((index + offset) & 0x7FFFFFFF) % entries.length;
      offset = offset*2 + 1;

      if (offset == -1)
        offset = 2;
    }

    // we found the right position, now do the removal
    if (entries[index] != null) {
      //! System.out.println("  k:" + entries[index].value);
      //! entries[index].value = null;
      elements--;
      entries[index] = DELETED;
    }
  }

  /**
   * INTERNAL: Figures out correct size for rehashed set, then does
   * the rehash.
   */
  protected void rehash() {
    // do we need to increase capacity, or are there so many
    // deleted objects hanging around that rehashing to the same
    // size is sufficient? if 5% (arbitrarily chosen number) of
    // cells can be freed up by a rehash, we do it.
    
    int gargagecells = entries.length - (elements + freecells);
    if (gargagecells / (double) entries.length > 0.05)
      // rehash with same size
      rehash(entries.length);
    else
      // rehash with increased capacity
      rehash(entries.length*2 + 1);
  }
  
  private void rehash(int newCapacity) {
    int oldCapacity = entries.length;

    SoftEntry[] newEntries = new SoftEntry[newCapacity];

    for (int ix = 0; ix < oldCapacity; ix++) {
      SoftEntry o = entries[ix];
      if (o == null || o == DELETED)
        continue;

      int hash = o.keyhash;
      int index = (hash & 0x7FFFFFFF) % newCapacity;
      int offset = 1;

      // search for the object
      while(newEntries[index] != null) { // no need to test for duplicates
        index = ((index + offset) & 0x7FFFFFFF) % newCapacity;
        offset = offset*2 + 1;

        if (offset == -1)
          offset = 2;
      }

      newEntries[index] = o;
    }

    entries = newEntries;
    freecells = entries.length - elements;
  }

  public int size() {
    processQueue();
    int size = 0;
    for (int ix = 0; ix < entries.length; ix++) {
      SoftEntry<K, E> o = entries[ix];
      if (o != null && o != DELETED)
        size++;
    }
    return size;
  }

  public void writeReport(java.io.Writer out) throws java.io.IOException {
    SoftEntry[] _entries = entries; // copy reference to avoid concurrent change
    out.write("<table>\n");
    for (int ix = 0; ix < _entries.length; ix++) {
      SoftEntry o = _entries[ix];
      if (o == null || o == DELETED)
        continue;
      Object key = o.get();
      Object val = o.value;      
      out.write("<tr><td>");
      out.write((key == null ? "null" : StringUtils.escapeHTMLEntities(key.toString())));
      out.write("</td><td>");
      out.write((val == null ? "null" : StringUtils.escapeHTMLEntities(val.toString())));
      out.write("</td></tr>\n");
    }
    out.write("</table><br>\n");
  }

  // --- SoftEntry

  public static class SoftEntry<K, E> extends SoftReference<K> {
    public int keyhash; // hashcode of key
    public E value; // the value the key is bound to

    // only used for DELETED object
    public SoftEntry() {
      super(null);
    }
    
    public SoftEntry(K key, E value, ReferenceQueue queue) {
      super(key, queue);
      this.keyhash = key.hashCode();
      this.value = value;
    }

    public K getKey() {
      return this.get();
    }

    public E getValue() {
      return this.value;
    }
    
    // NOTE: okey is /not/ a SoftEntry object, but an actual key
    public boolean equals(Object okey) {
      // no point in checking hashcode, since we already did that
      Object key = this.get();
      return (key != null) && (key == okey || key.equals(okey));
    }
  }

}
