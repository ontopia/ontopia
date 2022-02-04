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
 * INTERNAL: Grabber that maintains a cache of previously grabbed
 * objects. It works with any implementation of GrabberIF.</p>
 * 
 * The cache is first checked to see if a grabbed version already
 * exists. Otherwise the object is grabbed and the cache is
 * updated.</p>
 *
 * If the underlying grabber is modified or replaced, the cache is not
 * refreshed automatically. This has to be done explicitly.</p>
 */

@Deprecated
public class CachedGrabber<O, G> implements GrabberIF<O, G>, CachedIF {

  protected GrabberIF<O, G> grabber;
  protected Map<O, G> cache = new HashMap<O, G>();
  
  public CachedGrabber(GrabberIF<O, G> grabber) {
    this.grabber = grabber;
  }

  /**
   * Gets the grabber that is being cached.
   */
  public GrabberIF<O, G> getGrabber() {
    return grabber;
  }
  
  /**
   * Sets the grabber that is to be cached. Note that the cache is not
   * refreshed. If the cache is to be cleared call the refresh()
   * method explicitly.
   */
  public void setGrabber(GrabberIF<O, G> grabber) {
    this.grabber = grabber;
  }
  
  @Override
  public G grab(O object) {
    if (object == null) return null;
    if (cache.containsKey(object)) return cache.get(object);
    G grabbed = grabber.grab(object);
    cache.put(object, grabbed);
    return grabbed;
  }
  
  @Override
  public void refresh() {
    cache.clear();
  }

}




