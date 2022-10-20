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

/**
 * INTERNAL: A transactional storage cache implementation. The cache
 * uses the transaction to lookup objects and relies on the fact that
 * PersistentIFs can store their own data.
 */

public class RWLocalCache extends AbstractLocalCache {

  protected ObjectStates ostates;

  RWLocalCache(RWTransaction txn, StorageCacheIF pcache) {
    super(txn, pcache);

    this.ostates = txn.ostates;
  }

  // -----------------------------------------------------------------------------
  // StorageCacheIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public boolean exists(StorageAccessIF access, IdentityIF identity) {
    // ISSUE: could improve performance if we could keep track of
    // which fields actually were dirty. currently all fields of dirty
    // objects are being retrieved through the local storage access.

    // check object state
    int s = ostates.getState(identity);
    if (((s & ObjectStates.STATE_CREATED) == ObjectStates.STATE_CREATED) || 
        ((s & ObjectStates.STATE_DIRTY) == ObjectStates.STATE_DIRTY)) {
      // object exists if it's created/dirty in this txn
      return true;
    } else if ((s & ObjectStates.STATE_DELETED) == ObjectStates.STATE_DELETED) {
      // object does not exists if it's created/dirty in this txn
      return false;
    }

    // check parent cache
    if (pcache != null) {
      return pcache.exists(access, identity);
    }
    
    // check database
    return access.loadObject(this, identity);
  }
  
  @Override
  public Object getValue(StorageAccessIF access, IdentityIF identity, int field) {
    // check object state
    int s = ostates.getState(identity);

    // NOTE: if we get here we've checked the object itself already
    if ((s & ObjectStates.STATE_DELETED) == ObjectStates.STATE_DELETED) {
      return null;
    }

    // get value from cache if object not dirty    
    if (!(((s & ObjectStates.STATE_CREATED) == ObjectStates.STATE_CREATED) || 
          ((s & ObjectStates.STATE_DIRTY) == ObjectStates.STATE_DIRTY))) {
      // check parent cache
      if (pcache != null) {
        return pcache.getValue(access, identity, field);
      }
    }
    // otherwise get directly from database
    return access.loadField(this, identity, field);
  }

  // -----------------------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------------------

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("proxy.RWLocalCache@");
    sb.append(System.identityHashCode(this));
    if (pcache != null) {
      sb.append(" [parent = ").append(pcache).append(']');
    }
    return sb.toString();
  }
  
}
