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
import org.apache.commons.collections4.map.ReferenceMap;

/**
 * INTERNAL: 
 */

public class TransactionalSoftHashMapIndex<K, V>
  extends ReferenceMap<K, V> implements TransactionalLookupIndexIF<K, V> {
  private static final long serialVersionUID = 1L;

  public TransactionalSoftHashMapIndex() {
    super(ReferenceStrength.SOFT, ReferenceStrength.HARD);
  }

  @Override
  public void removeAll(Collection<K> keys) {
    for (K key : keys) {
      remove(key);
    }
  }

  @Override
  public void commit() {    
    // no-op
  }

  @Override
  public void abort() {
    // no-op
  }

}
