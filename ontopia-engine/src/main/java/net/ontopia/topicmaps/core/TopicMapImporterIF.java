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

/**
 * PUBLIC: A topic map importer interface which enables the
 * destination of the importation to be given, but allows the source
 * from which the importer reads its input topic map to be implicit
 * and implementation-dependent.</p>
 *
 * @see <code> net.ontopia.core.topicmaps.TopicMapReaderIF </code>
 * @see <code> net.ontopia.core.topicmaps.TopicMapWriterIF </code>
 */

public interface TopicMapImporterIF {

  /**
   * PUBLIC: Imports an implicitly designated topic map into the given topic map.
   *
   * @param topicmap The topic map into which the import will be done;
   *                 an object implementing TopicMapIF.
   */
  public void importInto(TopicMapIF topicmap) throws IOException;
  
}





