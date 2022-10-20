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
 * INTERNAL: A transactional storage cache implementation. The cache
 * uses the transactions identity map to lookup objects and stores its
 * cache entries directly on the PersistentIFs.
 */

public class StatisticsCache implements StorageCacheIF {

  protected int total_o;
  protected int total_f;
  protected int hits_o;
  protected int hits_f;
  protected int misses_o;
  protected int misses_f;

  protected int dump_interval = 100;
  protected String name;
  protected StorageCacheIF pcache;
  
  StatisticsCache(String name, StorageCacheIF parent_cache, int dump_interval) {
    this.name = name;
    this.pcache = parent_cache;
    this.dump_interval = dump_interval;
  }

  // -----------------------------------------------------------------------------
  // StorageCacheIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public AccessRegistrarIF getRegistrar() {
    return pcache.getRegistrar();
  }
  
  @Override
  public void close() {
    pcache.close();
  }

  @Override
  public boolean exists(StorageAccessIF access, IdentityIF identity) {
    total_o++;
    if (pcache.isObjectLoaded(identity)) {
      hits_o++;
    } else {
      misses_o++;
    }    
    if (total_o % dump_interval == 0) {
      dump();
    }
    return pcache.exists(access, identity);
  }
  
  @Override
  public Object getValue(StorageAccessIF access, IdentityIF identity, int field) {
    total_f++;
    if (pcache.isFieldLoaded(identity, field)) {
      hits_f++;
    } else {
      misses_f++;
    }    
    if (total_f % dump_interval == 0) {
      dump();
    }
    return pcache.getValue(access, identity, field);
  }

  @Override
  public boolean isObjectLoaded(IdentityIF identity) {
    return pcache.isObjectLoaded(identity);
  }

  @Override
  public boolean isFieldLoaded(IdentityIF identity, int field) {
    return pcache.isFieldLoaded(identity, field);
  }

  @Override
  public void registerEviction() {
    pcache.registerEviction();
  }
  
  @Override
  public void releaseEviction() {
    pcache.releaseEviction();
  }

  @Override
  public void evictIdentity(IdentityIF identity, boolean notifyCluster) {
    pcache.evictIdentity(identity, notifyCluster);
  }

  @Override
  public void evictFields(IdentityIF identity, boolean notifyCluster) {
    pcache.evictFields(identity, notifyCluster);
  }

  @Override
  public void evictField(IdentityIF identity, int field, boolean notifyCluster) {
    pcache.evictField(identity, field, notifyCluster);
  }  

  @Override
  public void clear(boolean notifyCluster) {
    pcache.clear(notifyCluster);
  }

  // -----------------------------------------------------------------------------
  // prefetch
  // -----------------------------------------------------------------------------

  @Override
  public int prefetch(StorageAccessIF access, Class<?> type, int field, int nextField, boolean traverse, Collection<IdentityIF> identities) {
    return pcache.prefetch(access, type, field, nextField, traverse, identities);
  }

  // -----------------------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------------------

  protected int percent(int c, int total) {
    if (c == 0) {
      return 0;
    }
    return Math.round(((100.0f*c)/(1.0f*total)));
  }

  public void dump() {
    System.out.println("StatisticsCache: " + name);
    System.out.println("  object hits: " + hits_o + " (" + percent(hits_o, total_o) + "%)");
    System.out.println("  object misses: " + misses_o  + " (" + percent(misses_o, total_o) + "%)");
    System.out.println("  field hits: " + hits_f + " (" + percent(hits_f, total_f) + "%)");
    System.out.println("  field misses: " + misses_f + " (" + percent(misses_f, total_f) + "%)");
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("proxy.StatisticsCache@");
    sb.append(System.identityHashCode(this));
    if (pcache != null) {
      sb.append(" [parent = ").append(pcache).append(']');
    }
    return sb.toString();
  }
  
}
