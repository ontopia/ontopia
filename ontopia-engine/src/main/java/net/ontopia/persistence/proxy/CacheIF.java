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

/**
 * INTERNAL: Simple interface used by innermost caches.
 */

public interface CacheIF<K, V> {

  public V get(K key);

  public V put(K key, V value);

  public V remove(K key, boolean notifyCluster);

  public void removeAll(Collection<K> keys, boolean notifyCluster);

  public void clear(boolean notifyCluster);

  public void writeReport(java.io.Writer out, boolean dumpCache) throws java.io.IOException;
  
}
