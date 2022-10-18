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

import java.util.Collection;
import java.util.Iterator;

/**
 * INTERNAL: Filters the objects in an iterator.<p>
 * 
 * Classes implementing the filter method of this interface must
 * return a collection containing a subset of the elements in the
 * iterator given to it.<p>
 */

public interface FilterIF<T> {

  /**
   * INTERNAL: Filters the input iterator and returns a collection
   * containing a subset of the iterator's elements.
   */
  Collection<T> filter(Iterator<T> objects);
  
}




