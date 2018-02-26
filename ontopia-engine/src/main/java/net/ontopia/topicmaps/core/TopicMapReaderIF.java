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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * PUBLIC: A topic map reader is used to read topic maps from an
 * implementation specific, implicit source.</p>
 *
 * @see <code> net.ontopia.topicmaps.core.TopicMapImporterIF </code>
 * @see <code> net.ontopia.topicmaps.core.TopicMapWriterIF </code>
 */

public interface TopicMapReaderIF {

  /**
   * PUBLIC: Reads the next topic map available from some implicit,
   * implementation dependent source.</p>
   *
   * A topic map source may contain multiple topic maps. The read
   * method returns the next topic map that is available from that
   * source. <code>null</code> is returned when there are no more
   * topic maps available. In a sense this is iterator-like
   * behaviour.</p>
   *
   * @exception IOException Thrown if reading the source fails.
   *
   * @return The next topic map read from the source; an object
   * implementing TopicMapIF. null is returned when there are no more
   * topic maps available from the source.
   */
  TopicMapIF read() throws IOException;

  /**
   * PUBLIC: Reads all the topic map available from some implicit,
   * implementation dependent source.</p>
   *
   * A topic map source may contain multiple topic maps. The readAll
   * method returns a collection contain all the topic maps available
   * from the source.</p>
   *
   * @exception IOException Thrown if reading the source fails.
   *
   * @return A collection containing all the topic maps read from the
   * source; objects implementing TopicMapIF.
   */
  Collection<TopicMapIF> readAll() throws IOException;
  
  /**
   * PUBLIC: Imports an implicitly designated topic map into the given topic map.
   *
   * @param topicmap The topic map into which the import will be done;
   *                 an object implementing TopicMapIF.
   */
  public void importInto(TopicMapIF topicmap) throws IOException;  

  /**
   * PUBLIC: set additional properties to the topic map reader. The
   * set of accepted properties differs per reader implementation, see
   * the specific reader documentation for details on accepted properties.
   * @param properties Additional properties for the reader
   */
  void setAdditionalProperties(Map<String, Object> properties);
}
