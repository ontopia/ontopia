/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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
package net.ontopia.topicmaps.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.entry.AbstractURLTopicMapReference;

/**
 * Service definition for services that provide format reader and writers.
 * @since 5.4.0
 */
public interface ImportExportServiceIF {

  /**
   * PUBLIC: Indicates that the service can supply a TopicMapReaderIF implementation for the specified
   * resource.
   * @param resource The resource a reader is needed for
   * @return true if this service can provide the needed reader
   */
  boolean canRead(URL resource);

  /**
   * PUBLIC: Indicates that the service can supply a TopicMapWriterIF implementation for the specified
   * resource.
   * @param resource The resource a writer is needed for
   * @return true if this service can provide the needed writer
   */
  boolean canWrite(URL resource);

  /**
   * PUBLIC: Create and return a TopicMapWriterIF for the specified stream. This method should be 
   * preceded with a call to {@link #canWrite(java.lang.String)} to check if this service can
   * write to the resource which the stream is connected to.
   * @param stream The stream to write to
   * @return The TopicMapWriterIF created by this service
   * @throws IOException if anything goes wrong during initialization of the writer regarding
   * IO operations
   */
  TopicMapWriterIF getWriter(OutputStream stream) throws IOException;

  /**
   * PUBLIC: Create and return a TopicMapReaderIF for the specified resource. This method should be 
   * preceded with a call to {@link #canRead(java.lang.String)} to check if this service can
   * read the specified resource.
   * @param resource The resource to create a reader for
   * @return The TopicMapReaderIF created by this service
   */
  TopicMapReaderIF getReader(URL resource);

  /**
   * PUBLIC: Creates an AbstractURLTopicMapReference for the specified url, using the appropriate
   * reader and/or writer provided by this service. This method is used by the sources like
   * URLTopicMapSource and ResourceTopicMapSource.
   * @param url The resource to create the reference for
   * @param referenceId The id to use for the reference
   * @param title The title to use for the reference
   * @param baseAddress The base address to use if supported
   * @return the reference for the specified resource
   */
  AbstractURLTopicMapReference createReference(URL url, String referenceId, String title, LocatorIF baseAddress);

}
