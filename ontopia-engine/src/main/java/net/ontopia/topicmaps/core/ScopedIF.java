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

import java.util.Collection;
  
/**
 * PUBLIC: Implemented by topic map objects that have scope. This
 * interface is implemented by AssociationIF, TopicIF, TopicNameIF,
 * VariantNameIF (through inheritance from TopicNameIF), OccurrenceIF
 * and TopicMapIF.</p>
 * 
 * The scope of an object is used to model its validity, effectivity
 * or relevance in a specific context. A scope is a set
 * of topics, and is present on all objects which may have it.
 * Scoping in topic maps is only significant for topic characteristic
 * assignments, and is inherited from parent objects.</p>
 */

public interface ScopedIF extends TMObjectIF {
  
  /**
   * PUBLIC: Returns the set of topics that are the stated scope of
   * this object. Scoping topics inherited from parent objects are not
   * included. There is no guarantee as to which order these topics
   * are returned in.
   *
   * @return A collection of TopicIF objects.
   */
  Collection<TopicIF> getScope();

  /**
   * PUBLIC: Add a topic to this scope. (A topic used in a scope is
   * also called a theme.) If the topic is already part of the scope
   * the method call has no effect.
   *
   * @param theme A topic to be added to this scope; an object
   * implementing TopicIF.
   */
  void addTheme(TopicIF theme);

  /**
   * PUBLIC: Remove a topic from this scope. If the topic is not already
   * in the scope this method has no effect.
   * (A topic used in a scope is also called a theme.)
   *
   * @param theme A topic to be removed from this scope; an object implementing TopicIF.
   */
  void removeTheme(TopicIF theme);
  
}
