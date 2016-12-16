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

import java.io.IOException;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

/**
 * Interface that describes a fulltext indexation service for use in {@link InMemoryTopicMapStore}.
 */
public interface FulltextImplementationIF {
  
  void initialize(InMemoryTopicMapStore store) throws IOException;
  SearcherIF getSearcher() throws IOException;
  IndexerIF getIndexer(boolean replaceIndex) throws IOException;
  void close() throws IOException;
}
