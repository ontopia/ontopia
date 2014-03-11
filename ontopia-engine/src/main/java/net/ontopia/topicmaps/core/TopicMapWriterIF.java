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
import java.util.Map;

/**
 * PUBLIC: A topic map writer is used to write/export topic maps in an
 * implementation dependent way to an implicit destination.</p>
 *
 * @see <code>net.ontopia.core.topicmaps.TopicMapReaderIF</code>
 * @see <code>net.ontopia.core.topicmaps.TopicMapImporterIF</code>
 */

public interface TopicMapWriterIF {

  /**
   * PUBLIC: Writes the given topic map to an implicit implementation
   * dependent destination. The write method will close any resources
   * opened internally. This means that the write method can
   * only be called once if the stream/writer was opened internally.
   *
   * @exception IOException Thrown if writing the topic map fails.
   *
   * @param source_topicmap The topic map to be exported/written;
   *                         an object implementing TopicMapIF
   */
  public void write(TopicMapIF source_topicmap) throws IOException;
  
  /**
   * PUBLIC: set additional properties to the topic map writer. The
   * set of accepted properties differs per writer implementation, see
   * the specific writer documentation for details on accepted properties.
   * @param properties Additional properties for the writer
   */
  public void setAdditionalProperties(Map<String, Object> properties);
}
