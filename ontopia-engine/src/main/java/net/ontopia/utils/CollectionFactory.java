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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL: A collection factory that returns non-synchronized standard
 * java.util collection objects.</p>
 */

public class CollectionFactory implements CollectionFactoryIF, java.io.Serializable {

  private static final long serialVersionUID = -4670702015296061304L;
  protected int initsize;

  public CollectionFactory() {
    initsize = 4;
  }

  public CollectionFactory(int initsize) {
    this.initsize = initsize;
  }

  @Override
  public <T> Set<T> makeSmallSet() {
    return new HashSet<>(initsize);
  }

  @Override
  public <T> Set<T> makeLargeSet() {
    return new HashSet<>();
  }

  @Override
  public <V, K> Map<V, K> makeSmallMap() {
    return new HashMap<>(initsize);
  }

  @Override
  public <V, K> Map<V, K> makeLargeMap() {
    return new HashMap<>();
  }
  
  @Override
  public <T> List<T> makeSmallList() {
    return new ArrayList<>(initsize);
  }

  @Override
  public <T> List<T> makeLargeList() {
    return new ArrayList<>();
  }

}
