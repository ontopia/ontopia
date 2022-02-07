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
import java.util.Objects;

/**
 * INTERNAL: Comparator that stringifies the arguments and compares them
 * using another comparator. DefaultStringifier is the default
 * stringifier.</p>
 */

@Deprecated
public class StringifierComparator<T> implements Comparator<T> {
  
  protected Comparator<? super T> comparator;
  protected StringifierIF<T> stringifier;

  public StringifierComparator() {
    stringifier = Objects::toString;
  }

  public StringifierComparator(StringifierIF<T> stringifier) {
    this.stringifier = stringifier;
  }
  
  public StringifierComparator(StringifierIF<T> stringifier, Comparator<? super T> comparator) {
    this.stringifier = stringifier;
    this.comparator = comparator;
  }

  /**
   * Gets the comparator which is to be used.
   */
  public Comparator<? super T> getComparator() {
    return comparator;
  }
  
  /**
   * Sets the comparator which is to be used.
   */
  public void setComparator(Comparator<? super T> comparator) {
    this.comparator = comparator;
  }
  
  @Override
  public int compare(T obj1, T obj2) {
    if (Objects.equals(obj1, obj2)) return 0;

    String name1 = stringifier.toString(obj1);
    String name2 = stringifier.toString(obj2);

    if (name1 == null)
      return (name2 == null ? 0 : 1);
    else
      if (name2 == null)
        return -1;
      else {
        int result = name1.compareToIgnoreCase(name2);
        // If the decision has been made
        if (result != 0 || comparator == null) return result;        
        // Use comparator when equally ranked
        return comparator.compare(obj1, obj2);            
      }
  }
  
}




