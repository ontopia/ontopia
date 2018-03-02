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
 * PUBLIC: Implemented by topic map objects that are instances of a
 * single type, such as occurrences, associations, association roles,
 * and topic names.</p>
 *
 * Types are always represented by topics.</p>
 */

public interface TypedIF extends TMObjectIF {

  /**
   * PUBLIC: Gets the type that this object is an instance of.
   *
   * @return The type of this object; an object implementing TopicIF.
   */
  TopicIF getType();

  /**
   * PUBLIC: Sets the type that this object is an instance of.
   *
   * @param type The type for this object; an object implementing TopicIF.
   */
  void setType(TopicIF type);
  
}
