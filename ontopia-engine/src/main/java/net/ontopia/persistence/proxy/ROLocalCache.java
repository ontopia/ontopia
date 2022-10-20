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
 * INTERNAL: A transactional read-only storage cache
 * implementation.
 */

public class ROLocalCache extends AbstractLocalCache {

  ROLocalCache(ROTransaction txn, StorageCacheIF pcache) {
    super(txn, pcache);
  }

  // -----------------------------------------------------------------------------
  // StorageCacheIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public boolean exists(StorageAccessIF access, IdentityIF identity) {
    // check parent cache
    if (pcache != null) {
      return pcache.exists(access, identity);
    }
    
    // check database
    return access.loadObject(this, identity);
  }
  
  @Override
  public Object getValue(StorageAccessIF access, IdentityIF identity, int field) {
    // check parent cache
    if (pcache != null) {
      return pcache.getValue(access, identity, field);
    }
    
    // otherwise get directly from database
    return access.loadField(this, identity, field);
  }

  // -----------------------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------------------

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("proxy.ROLocalCache@");
    sb.append(System.identityHashCode(this));
    if (pcache != null) {
      sb.append(" [parent = ").append(pcache).append(']');
    }
    return sb.toString();
  }
  
}
