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

package net.ontopia.infoset.fulltext.core;

import java.io.IOException;

/**
 * INTERNAL: Represents a search engine indexer. Instances of this class
 * are able to index documents and generate an index which can be used
 * for searching those documents.<p>
 */

public interface IndexerIF {

  /**
   * INTERNAL: Indexes the specified document. This includes tokenizing,
   * indexing and storing the document fields.
   */
  public void index(DocumentIF document) throws IOException;
  
  /**
   * INTERNAL: Removes all documents with the specified field value from
   * the index. This method should generally be, but is not limited
   * to, used to delete documents by their identity field.<p>
   *
   * @return The number of documents that was deleted from the
   * index. -1 is return if this number is unknown.
   */
  public int delete(String field, String value) throws IOException;
    
  /**
   * INTERNAL: Flushes all changes done to the index. A flushing
   * operation can include actions like persisting changes and
   * optimizing the index.
   */
  public void flush() throws IOException;
  
  /**
   * INTERNAL: Deletes the index. The indexer is closed after this
   * method returns. Note that some indexers might not support this
   * operation.
   *
   * @since 1.3
   */
  public void delete() throws IOException;

  /**
   * INTERNAL: Closes the indexer. After the indexer has been closed it
   * cannot (generally) be reopened.
   */
  public void close() throws IOException;
  
}
