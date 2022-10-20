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

import java.util.Comparator;
import java.util.Iterator;

/**
 * INTERNAL: Comparator for Iterators.  Compares each element in turn
 * until the end of one Iterator is reached.  Then, if one Iterator
 * contains fewer elements than the other, it is ordered first.
 */

public class IteratorComparator<T> implements Comparator<Iterator<T>> {
  private Comparator<? super T> elementComparator;
  
  public IteratorComparator (Comparator<? super T> elementComparator) {
    this.elementComparator = elementComparator;
  }
  
  @Override
  public int compare(Iterator<T> it1, Iterator<T> it2) {
    int retVal = 0;
    
    // Iterate until difference is is found or reached end of one or both
    // iterators.
    while (retVal == 0 && it1.hasNext() && it2.hasNext()) {
      retVal = elementComparator.compare(it1.next(), it2.next());
    }
    
    // (Only) it1 has elements left, so it1 > it2.
    if (retVal == 0 && it1.hasNext()) {
      retVal = 1;
    }
    
    // (Only) it2 has elements left, so it1 < it2.
    if (retVal == 0 && it2.hasNext()) {
      retVal = -1;
    }
    return retVal;
  }

}
