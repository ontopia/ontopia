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
 * INTERNAL: Cluster implementation interface.
 */

public interface ClusterIF {
  
  // event types

  int QUERY_CACHE_SRCLOC_EVICT = CachesIF.QUERY_CACHE_SRCLOC;
  int QUERY_CACHE_SRCLOC_CLEAR = CachesIF.QUERY_CACHE_SRCLOC + 1;

  int QUERY_CACHE_SUBIND_EVICT = CachesIF.QUERY_CACHE_SUBIND;
  int QUERY_CACHE_SUBIND_CLEAR = CachesIF.QUERY_CACHE_SUBIND + 1;

  int QUERY_CACHE_SUBLOC_EVICT = CachesIF.QUERY_CACHE_SUBLOC;
  int QUERY_CACHE_SUBLOC_CLEAR = CachesIF.QUERY_CACHE_SUBLOC + 1;

  int QUERY_CACHE_RT1_EVICT = CachesIF.QUERY_CACHE_RT1;
  int QUERY_CACHE_RT1_CLEAR = CachesIF.QUERY_CACHE_RT1 + 1;

  int QUERY_CACHE_RT2_EVICT = CachesIF.QUERY_CACHE_RT2;
  int QUERY_CACHE_RT2_CLEAR = CachesIF.QUERY_CACHE_RT2 + 1;

  int DATA_CACHE_CLEAR = -1;

  int DATA_CACHE_IDENTITY_EVICT = -2;
  int DATA_CACHE_FIELDS_EVICT = -3;
  int DATA_CACHE_FIELD_EVICT = -4;
  
  void join();
  
  void leave();

  void flush();
  
  void evictIdentity(IdentityIF identity);

  void evictFields(IdentityIF identity);

  void evictField(IdentityIF identity, int field);

  void clearDatacache();
  
  void evictCache(IdentityIF namespace, int cacheType, Object key);

  void evictCache(IdentityIF namespace, int cacheType, Collection keys);

  void clearCache(IdentityIF namespace, int cacheType);
  
}
