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

package net.ontopia.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL: Stringifier that maintains a cache of previously
 * stringified objects. It works with any implementation of
 * StringifierIF.</p>
 * 
 * The cache is first checked to see if a stringified version already
 * exists. Otherwise the object is stringified and the cache is
 * updated.</p>
 */

@Deprecated
public class CachedStringifier<T> implements StringifierIF<T>, CachedIF {

  protected StringifierIF<? super T> stringifier;
  protected Map<T, String> cache = new HashMap<T, String>();
  
  public CachedStringifier(StringifierIF<? super T> stringifier) {
    this.stringifier = stringifier;
  }

  /**
   * Gets the stringifier that is to be cached.
   */
  public StringifierIF<? super T> getStringifier() {
    return stringifier;
  }
  
  /**
   * Sets the stringifier that is to be cached.
   */
  public void setStringifier(StringifierIF<? super T> stringifier) {
    this.stringifier = stringifier;
  }
    
  @Override
  public String toString(T object) {
    if (object == null) return "null";
    String string = cache.get(object);
    if (string != null) return string;
    String stringified = stringifier.toString(object);
    cache.put(object, stringified);
    return stringified;
  }

  @Override
  public void refresh() {
    cache.clear();
  }

}




