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

package net.ontopia.topicmaps.core;


/**
 * PUBLIC: Factory interface for creating new topic map store
 * objects.</p>
 *
 * Factories are used to make object creation independent of the
 * specific topic map implementation used. </p>
 */

public interface TopicMapStoreFactoryIF {

  /**
   * PUBLIC: Creates a topic map store object.
   *
   * @return An object implementing TopicMapStoreIF
   */
  public TopicMapStoreIF createStore();
  
}
