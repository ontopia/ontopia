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

import java.util.Set;
import java.util.List;
import java.util.Map;

/**
 * INTERNAL: Factory that creates collection objects.</p>
 *
 * Implementations should be made if it is necessary to work with
 * customized collection classes. Classes that use a collection
 * factory instead of creating collection objects itself, will be able
 * to work with multiple collection implementations.</p>
 */
public interface CollectionFactoryIF {

  /**
   * INTERNAL: Creates a set that is expected to contain a small
   * number of objects.
   */
  <T> Set<T> makeSmallSet();

  /**
   * INTERNAL: Creates a set that is expected to contain a large
   * number of objects.
   */
  <T> Set<T> makeLargeSet();

  /**
   * INTERNAL: Creates a map that is expected to contain a small
   * number of objects.
   */
  <V, K> Map<V, K> makeSmallMap();

  /**
   * INTERNAL: Creates a map that is expected to contain a large
   * number of objects.
   */
  <V, K> Map<V, K> makeLargeMap();

  /**
   * INTERNAL: Creates a list that is expected to contain a small
   * number of objects.
   */
  <T> List<T> makeSmallList();

  /**
   * INTERNAL: Creates a list that is expected to contain a large
   * number of objects.
   */
  <T> List<T> makeLargeList();
  
}




