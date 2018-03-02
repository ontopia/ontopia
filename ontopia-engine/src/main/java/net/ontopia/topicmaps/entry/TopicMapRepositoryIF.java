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

package net.ontopia.topicmaps.entry;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicMapStoreIF;

/**
 * PUBLIC: Class used to provide easy access to references from one or
 * more topic map sources.<p>
 *
 * The repository allows access to its underlying references using
 * reference keys that the repository assigns each reference. It is up
 * to the repository implementation to generate these keys. Each
 * repository decides how to generate its reference keys. Note that
 * the reference key is therefore not neccessarily identical to the
 * reference's id. See the implementations for more information on
 * their key generation policies.<p>
 *
 * The default implementation of TopicMapRepositoryIF is thread-safe.<p>
 *
 * @since 1.3.2
 */
public interface TopicMapRepositoryIF {
  
  /**
   * PUBLIC: Creates a new topic map store for the given topic map
   * id. This method effectively delegates the call to the underlying
   * topic map reference. An exception is thrown if the topic map
   * reference cannot be found or any errors occurs while loading it.
   *
   * @since 3.4
   */
  TopicMapStoreIF createStore(String refkey, boolean readonly);
  
  /**
   * PUBLIC: Gets a topic map reference by its reference key. Returns
   * null if not found.
   */
  TopicMapReferenceIF getReferenceByKey(String refkey);

  /**
   * PUBLIC: Gets the key used to identify the reference in the
   * repository.
   */
  String getReferenceKey(TopicMapReferenceIF ref);
  
  /**
   * PUBLIC: Returns a collection containing all references.
   */
  Collection<TopicMapReferenceIF> getReferences();
  // returns TopicMapReferenceIF objects; unmodifiable
  // no removeReference; do reference.deactivate or reference.delete instead
  
  /**
   * PUBLIC: Returns a collection containing the keys of all references.
   */
  Collection<String> getReferenceKeys();

  /**
   * PUBLIC: Refreshes all sources and recreates the reference map.
   */
  void refresh();
  // clear reference Map, refresh all sources, recreate reference Map

  /**
   * PUBLIC: Returns the topic map source that has the given source id.
   */
  TopicMapSourceIF getSourceById(String source_id);

  /**
   * PUBLIC: Returns an immutable collection containing the
   * TopicMapSourceIFs registered with the topic map repository.
   */
  Collection<TopicMapSourceIF> getSources();
  // FIXME: Is the collection updated when the repository is updated?
  
  /**
   * PUBLIC: Adds the source to the repository.
   */
  void addSource(TopicMapSourceIF source);
  // if source already present; do nothing
  // disallow "." in source IDs
  // while (source.getId() is a duplicate) { source.setId(origId + num++); }

  /**
   * PUBLIC: Removes the source from the repository.
   */
  void removeSource(TopicMapSourceIF source);

  /**
   * PUBLIC: Closes the repository and releases all resources bound by
   * the repository. Closing the repository will also close any open
   * topic map references held by the topic map repository as well as
   * the topic map sources added to it.
   *
   * @since 2.1
   */
  void close();

}
