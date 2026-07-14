/*-
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2026 The Ontopia Project
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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Minimalistic implementation of Striped locking, based on Google Guava's Striped class.
 * Avoids having to add Guava as a dependency to hotfix a single issue.
 * @see https://github.com/google/guava
 * @param <L> The type of lock
 */
public class Striped<L> {

  private static final int COUNT = 1024;
  private static final Striped<Lock> INSTANCE = new Striped<Lock>();
  private static final Lock[] LOCKS = new Lock[COUNT + 1];

  static {
    for (int i = 0; i <= COUNT; i++) {
      LOCKS[i] = new ReentrantLock();
    }
  }

  private Striped() { /* no-op */ }

  public static Striped<Lock> getInstance() {
    return INSTANCE;
  }

  final int indexFor(Object key) {
    int hash = smear(key.hashCode());
    return hash & COUNT;
  }

  public final L get(Object key) {
    return getAt(indexFor(key));
  }

  // Copied from java/com/google/common/collect/Hashing.java
  private static int smear(int hashCode) {
    hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
    return hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);
  }

  public L getAt(int index) {
    return (L) LOCKS[index];
  }
}
