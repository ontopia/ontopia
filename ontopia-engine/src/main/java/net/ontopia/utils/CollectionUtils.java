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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * INTERNAL: Class that contains useful collection methods.
 */

public class CollectionUtils {

  private static final Random random = new Random();

  private CollectionUtils() {
  }
  
  /**
   * INTERNAL: Gets the first object in the collection. If the
   * collection is empty, null is returned.
   */
  public static <T> T getFirst(Collection<T> coll) {
    return (coll == null) || coll.isEmpty() ? null : coll.iterator().next();
  }

  /**
   * INTERNAL: Gets the first object in the collection. If the
   * collection does not contain any elements NoSuchElementException
   * is thrown.<p>
   *
   * @since 1.3.4
   */
  public static <T> T getFirstElement(Collection<T> coll) {
    if ((coll == null) || coll.isEmpty()) {
      throw new NoSuchElementException();
    }
    return coll.iterator().next();
  }

  /**
   * INTERNAL: Gets a random object from the collection. If the
   * collection is empty, null is returned.
   */
  public static <T> T getRandom(Collection<T> coll) {

    if (coll == null || coll.isEmpty()) {
      return null;
    }
    int chosen = random.nextInt(coll.size());

    // If it's a list return it directly
    if (coll instanceof List) {
      return ((List<T>)coll).get(chosen);
    }

    // Otherwise loop through the collection
    Iterator<T> iter = coll.iterator();
    for (int i = 0; i < chosen - 1; i++) {
      iter.next();
    }
    return iter.next();
  }

  /**
   * INTERNAL: Compares two collections to see if they contain the same
   * elements.
   *
   * @since 1.4.1
   */
  public static <T> boolean equalsUnorderedSet(Collection<T> coll1, Collection<T> coll2) {
    
    // Take care of nulls
    if (coll1 == null) {
      if (coll2 == null) {
        // 1: null 2: null
        return true;
      } else {
        // 1: null 2: not null
        return false;
      }
    } else
      if (coll2 == null) {
        // 1: not null 2: null
        return false;
    }
    
    // Compare set size
    int size1 = coll1.size();
    int size2 = coll2.size();    
    if (size1 != size2) {
      return false;
    }
    
    // If both have 1 element compare first element
    if (size1 == 1) {      
      return Objects.equals(coll1.iterator().next(), coll2.iterator().next());
    }
    
    // Compare collections as sets
    if (coll1 instanceof Set) {
      if (coll2 instanceof Set) {
        return coll1.equals(coll2);
      } else {
        return coll1.equals(new HashSet<T>(coll2));
      }
    } else if (coll2 instanceof Set) {
      return coll2.equals(new HashSet<T>(coll1));
    } else {
      return new HashSet<T>(coll2).equals(new HashSet<T>(coll1));
    }
  }

  /**
   * EXPERIMENTAL: Iterates over up to <i>length</i> number of
   * elements in the iterator and returns those elements as a
   * Collection. If the iterator is exhausted only the iterated
   * elements are returned.
   */
  public static <T> List<T> nextBatch(Iterator<T> iter, int length) {
    List<T> batch = new ArrayList<T>(length);
    int i = 0;
    do {
      batch.add(iter.next());
      i++;
    } while (i < length && iter.hasNext());
    return batch;
  }
}
