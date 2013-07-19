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

import java.util.Enumeration;
import java.util.Iterator;

/**
 * INTERNAL: A wrapper class for traversing enumerations as iterators.
 */

public class EnumerationIterator<E> implements Iterator<E> {

  protected Enumeration<E> enumeration;
  
  public EnumerationIterator(Enumeration<E> enumeration) {
    this.enumeration = enumeration;
  }
  
  public boolean hasNext() {
    return enumeration.hasMoreElements();
  }

  public E next() {
    return enumeration.nextElement();
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
  
}
