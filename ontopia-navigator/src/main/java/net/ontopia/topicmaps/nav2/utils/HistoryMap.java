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

package net.ontopia.topicmaps.nav2.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Helper class for storing elements up to a certain amount,
 * lower most elements will be removed to ensure a fixed size of the
 * collection.
 *
 * @since 1.2.5
 */
public class HistoryMap<T> extends HashMap<Integer, T> {
  protected int maxEntries;
  protected boolean suppressDuplicates;
  private int counter;
  
  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(HistoryMap.class.getName());
  
  /**
   * Default constructor which uses 20 entries as the default history
   * size and does suppress duplicates.
   */
  public HistoryMap() {
    this(20, true);
  }
  
  /**
   * Constructor which allows to specify number of entries to store and
   * duplicate suppression behaviour.
   *
   * @param maxEntries Maxium number of entries to store in the history.
   * @param suppressDuplicates Should suppress duplicate entries in map.
   */
  public HistoryMap(int maxEntries, boolean suppressDuplicates) {
    super();
    this.maxEntries = maxEntries;
    this.counter = 0;
    this.suppressDuplicates = suppressDuplicates;
  }

  public int getMaxEntries() {
    return maxEntries;
  }

  public boolean doesSuppressDuplicates() {
    return suppressDuplicates;
  }
  
  public void add(T obj) {
    // do not add if object already exists
    if (suppressDuplicates && containsValue(obj)) {
      return;
    }
    counter++;
    put(counter, obj);
    if (size() >= maxEntries)
      try {
        remove(counter - maxEntries);
      } catch (Exception e) {
        log.error("Remove of entry from historymap without success." +
                  e.getMessage());
      }
  }

  public void removeEntry(T obj) {
    Iterator<Integer> it = keySet().iterator();
    log.info("A removing from history");
    while (it.hasNext()) {
      Integer key = it.next();
      T val = get(key);
      if (val.equals(obj)) {
        log.info("removing from history " + key);
        remove(key);
        break;
      }
    } // while it
  }
  
  public T getEntry(int index) {
    return get(counter - size() + index);
  }

  public Collection<T> getEntries() {
    Collection<T> result = new ArrayList<T>();
    for (int i=1; i <= size(); i++) {
      if (getEntry(i) != null) {
        result.add(getEntry(i));
      }
    }

    return result;
  }

  public Collection getEntriesReverse() {
    Collection result = new ArrayList();
    for (int i=size(); i >= 1; i--) {
      if (getEntry(i) != null) {
        result.add(getEntry(i));
      }
    }

    return result;
  }
  
}
