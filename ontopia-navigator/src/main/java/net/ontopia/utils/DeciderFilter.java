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
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * INTERNAL: Filter that filters a collection using a decider. The
 * decider is applied to the individual objects in the collection. If
 * the object is accepted by the decider it will become part of the
 * result.</p>
 */
public class DeciderFilter<T> implements FilterIF<T> {

  protected Predicate<T> decider;

  public DeciderFilter(Predicate<T> decider) {
    this.decider = decider;
  }
  
  @Override
  public Collection<T> filter(Iterator<T> objects) {
    // Initialize result
    List<T> result = new ArrayList<T>();

    // Loop over the objects
    while (objects.hasNext()) {
      T object = objects.next();
      // Add object to result if accepted by decider
      if (decider.test(object)) {
        result.add(object);
      }
    }
    return result;
  }

}
