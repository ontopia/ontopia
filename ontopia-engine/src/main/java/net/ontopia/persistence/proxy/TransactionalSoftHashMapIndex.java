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

package net.ontopia.persistence.proxy;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.utils.SoftHashMapIndex;

/**
 * INTERNAL: 
 */

public class TransactionalSoftHashMapIndex<K, E> 
  extends SoftHashMapIndex<K, E> implements TransactionalLookupIndexIF<K, E> {

  public void removeAll(Collection<K> keys) {
    Iterator<K> iter = keys.iterator();
    while (iter.hasNext()) {
      remove(iter.next());
    }
  }

  public void commit() {    
    // no-op
  }

  public void abort() {
    // no-op
  }

}
