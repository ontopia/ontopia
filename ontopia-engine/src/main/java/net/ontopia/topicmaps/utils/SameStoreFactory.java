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

package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

/**
 * PUBLIC: A store factory that always returns the store given to it
 * via its constructor. This class is useful when the intention is
 * that the same store object is always to be used.</p>
 */
public class SameStoreFactory implements TopicMapStoreFactoryIF {

  protected TopicMapStoreIF store;

  /**
   * PUBLIC: Creates a TopicMapStoreFactoryIF which persistently
   * references the given store
   *
   * @param store the given topicMapStoreFactoryIF
   */ 
  public SameStoreFactory(TopicMapStoreIF store) {
    this.store = store;
  }

  /**
   * PUBLIC: Returns a topicmap store, which is the store given to the
   * constructor.
   *
   * @return The store received through the object's constructor.
   */
  public TopicMapStoreIF createStore() {
    return store;
  }    
  
}
