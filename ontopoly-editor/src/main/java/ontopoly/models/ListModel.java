/*
 * #!
 * Ontopoly Editor
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
package ontopoly.models;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.wicket.model.LoadableDetachableModel;

public abstract class ListModel<T,V> extends LoadableDetachableModel<List<T>> {

  private List<V> values;

  public ListModel(List<V> values) {
    Objects.requireNonNull(values, "values parameter cannot be null.");
    this.values = values;
  }
  
  @Override
  protected List<T> load() {
    List<T> result = makeCollection(values.size());
    Iterator<V> iter = values.iterator();
    while (iter.hasNext()) {
      V value = iter.next();
      result.add(getObjectFor(value));
    }
    return result;
  }

  /**
   * Make new collection instance.
   * @param size the size of the collection to create.
   * @return return new collection
   */
  protected List<T> makeCollection(int size) {
    return new ArrayList<T>(size);
  }
  
  /**
   * This method will be called for each value in the containing collection. The result will be part of the final model collection.
   * @param object the object to wrap
   * @return the wrapper object
   */
  protected abstract T getObjectFor(V object);
  
}
