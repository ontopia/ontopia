/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
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
package net.ontopia.infoset.fulltext.core;

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

/**
 * Interface that describes a fulltext indexation service for use in {@link InMemoryTopicMapStore}.
 * @since 5.4.0
 */
public interface FulltextImplementationIF {

  void install(TopicMapReferenceIF reference);

  void storeOpened(TopicMapStoreIF store);

  void deleteIndex();

  void synchronize(TopicMapStoreIF store);

  void reindex();

  void close();
}
