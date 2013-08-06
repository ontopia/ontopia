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
import net.ontopia.utils.SoftHashMap;


/**
 * INTERNAL: Default caches.
 */

public class DefaultCaches implements CachesIF {
  
  DefaultCaches() {
    // NOTE: no need to hold references to the caches as only one copy
    // will every be requested anyway as we do not do any clustering.
  }
  
  public Map createDataCache() {
    return Collections.synchronizedMap(new SoftHashMap());
  }
  
  public CacheIF createCache(int cacheType, IdentityIF namespace) {
    switch (cacheType) {
    case CachesIF.QUERY_CACHE_SRCLOC:
      return createCache();
    case CachesIF.QUERY_CACHE_SUBIND:
      return createCache();
    case CachesIF.QUERY_CACHE_SUBLOC:
      return createCache();
    case CachesIF.QUERY_CACHE_RT1:
      return createCache();
    case CachesIF.QUERY_CACHE_RT2:
      return createCache();
    default:
      throw new OntopiaRuntimeException("Invalid cache type: " + cacheType);
    }
  }
  
  // --- helper

  private CacheIF createCache() {
    return new DefaultCache(new SoftHashMap());
  }
  
}
