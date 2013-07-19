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

package net.ontopia.topicmaps.query.core;

import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * PUBLIC: Interface for query language implementations. An instance of a
 * {@link QueryProcessorFactoryIF} create an appropriate
 * {@link QueryProcessorIF} for the provided {@link TopicMapIF}.
 * 
 * @since 5.1
 */
public interface QueryProcessorFactoryIF {
  /**
   * PUBLIC: Returns the query language that is used by this
   * {@link QueryProcessorFactoryIF} implementation.
   * 
   * @return the name of this {@link QueryProcessorFactoryIF} implementation.
   */
  public String getQueryLanguage();

  /**
   * PUBLIC: Creates a new {@link QueryProcessorIF} instance to query a given
   * topic map.
   * 
   * @param topicmap the topic map to be used by the query processor.
   * @param base base address of the topic map if known.
   * @param properties additional properties used to configure the query
   *          processor.
   * @return a {@link QueryProcessorIF} instance that can be used to query the
   *         topic map.
   */
  public QueryProcessorIF createQueryProcessor(TopicMapIF topicmap,
      LocatorIF base, Map<String, String> properties);
}
