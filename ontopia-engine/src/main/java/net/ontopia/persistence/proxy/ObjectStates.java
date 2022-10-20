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

import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;

/**
 * INTERNAL: A data structure that keeps track of the objects that has
 * been touched within the transaction boundaries. The state of the
 * objects are also maintained.
 */
public class ObjectStates {

  public static final int STATE_CREATED = 1;
  public static final int STATE_DELETED = 2;
  public static final int STATE_DIRTY = 4;
  public static final int STATE_READ = 8;

  protected TObjectIntHashMap<IdentityIF> map = new TObjectIntHashMap<IdentityIF>(53);
  protected boolean clean = true;

  ObjectStates() {
  }

  public synchronized boolean isClean() {
    return clean;
  }

  public synchronized int size() {
    return map.size();
  }

  public synchronized boolean isClean(IdentityIF identity) {
    int s = map.get(identity);
    return (((s & STATE_DIRTY) != STATE_DIRTY) &&
            ((s & STATE_CREATED) != STATE_CREATED) &&
            ((s & STATE_DELETED) != STATE_DELETED));
  }

  public synchronized boolean isCreated(IdentityIF identity) {
    int s = map.get(identity);
    return ((s & STATE_CREATED) == STATE_CREATED);
  }

  public synchronized void created(IdentityIF identity) {
    int s = map.get(identity);
    if ((s & STATE_CREATED) != STATE_CREATED) {
      // + created, -deleted
      s &= ~(STATE_DELETED);
      map.put(identity, s | STATE_CREATED);
      clean = false;
    }
  }

  public synchronized void deleted(IdentityIF identity) {
    int s = map.get(identity);
    if ((s & STATE_DELETED) != STATE_DELETED) {
      // + deleted, -created
      s &= ~(STATE_CREATED);
      map.put(identity, s | STATE_DELETED);
      clean = false;
    }
  }

  public synchronized void dirty(IdentityIF identity) {
    int s = map.get(identity);
    if ((s & STATE_DIRTY) != STATE_DIRTY) {
      map.put(identity, s | STATE_DIRTY);
      clean = false;
    }
  }

  public synchronized void read(IdentityIF identity) {
    int s = map.get(identity);
    if ((s & STATE_READ) != STATE_READ) {
      map.put(identity, s | STATE_READ);
    }
  }

  public synchronized int getState(IdentityIF identity) {
    return map.get(identity);
  }

  public synchronized void clear() {
    map = new TObjectIntHashMap<IdentityIF>(53);
    clean = true;
  }

  // --- events

  public synchronized void forEachEntry(TObjectIntProcedure<IdentityIF> proc) {
    map.forEachEntry(proc);
  }

}
