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
import org.apache.commons.collections4.iterators.IteratorChain;

/**
 * INTERNAL: An iterator that works as a facade for multiple
 * iterators. The iterator represents the sum of all the
 * iterators.
 */
@Deprecated
public class IteratorIterator<T> extends IteratorChain<T> {

  /**
   * @param colls_or_iters a collection of collections or iterators.
   */
  public IteratorIterator(Collection<Collection<T>> colls_or_iters) {
    super();
    for (Collection<T> col : colls_or_iters) {
      addIterator(col.iterator());
    }
  }

  public IteratorIterator(Iterator<Iterator<T>> colls_or_iters) {
    while (colls_or_iters.hasNext()) {
      addIterator(colls_or_iters.next());
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }  
}
