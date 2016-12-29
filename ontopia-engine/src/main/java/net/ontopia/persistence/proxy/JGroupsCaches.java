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

import java.util.Collections;
import java.util.Map;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;

/**
 * INTERNAL: Caches used by JGroups cluster.
 */

public class JGroupsCaches implements CachesIF {

  private final ClusterIF cluster;
  
  protected JGroupsCaches(ClusterIF cluster) {
    this.cluster = cluster;
  }
  
  public <K, V> Map<K, V> createDataCache() {
    return Collections.synchronizedMap(this.<K, V>createSoftHashMap());
  }
  
  public <K, V>CacheIF<K, V> createCache(int cacheType, IdentityIF namespace) {
    switch (cacheType) {
    case CachesIF.QUERY_CACHE_SRCLOC:
      return _createCache(cacheType, namespace);
    case CachesIF.QUERY_CACHE_SUBIND:
      return _createCache(cacheType, namespace);
    case CachesIF.QUERY_CACHE_SUBLOC:
      return _createCache(cacheType, namespace);
    case CachesIF.QUERY_CACHE_RT1:
      return _createCache(cacheType, namespace);
    case CachesIF.QUERY_CACHE_RT2:
      return _createCache(cacheType, namespace);
    default:
      throw new OntopiaRuntimeException("Invalid cache type: " + cacheType);
    }
  }

  // --- helper
    
  private <K, V> CacheIF<K, V> _createCache(int cacheType, IdentityIF namespace) {
    return new ClusteredCache<K, V>(this.<K, V>createSoftHashMap(), this.cluster, cacheType, namespace);
  }

  private <K, V> Map<K, V> createSoftHashMap() {
    return new ReferenceMap<K, V>(AbstractReferenceMap.ReferenceStrength.SOFT, AbstractReferenceMap.ReferenceStrength.HARD);
  }
}
