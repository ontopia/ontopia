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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * INTERNAL: An iterator that uses a decider to filter the elements of
 * another iterator.</p>
 */

@Deprecated
public class DeciderIterator<T> implements Iterator<T> {

  protected DeciderIF<T> decider;
  protected Iterator<T> iterator;

  protected boolean done;
  protected T next;
  
  public DeciderIterator(DeciderIF<T> decider, Iterator<T> iterator) {
    this.decider = decider;
    this.iterator = iterator;

    // Find the first applicable element.
    findNext();
  }

  protected void findNext() {
    // Loop over the remaining elements to find next applicable element.
    while (iterator.hasNext()) {
      T element = iterator.next();
      // Check to see if element is acceptable.
      if (decider.ok(element)) {
        next = element;
        return;
      }
    }
    // There are no more elements and we're done.
    done = true;
  }
  
  @Override
  public boolean hasNext() {
    // If we're done there are no more elements in this iterator.
    if (done) return false;
    // We're not done and there are more elements.
    return true;
  }

  @Override
  public T next() {
    // Throw exception if there are no more elements.
    if (done) throw new NoSuchElementException();
    // Locate next applicable element.
    T object = next;
    findNext();
    // Return element.
    return object;
  }

  @Override
  public void remove() {
    // Delegate remove to underlying iterator.
    iterator.remove();
  }
  
}
