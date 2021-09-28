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

package net.ontopia.infoset.fulltext.utils;

import java.io.IOException;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.DocumentProcessorIF;
import net.ontopia.infoset.fulltext.core.IndexerIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/**
 * INTERNAL: A standalone index manager that performs document
 * processing and indexing processes in the current thread. No other
 * threads are used.<p>
 *
 * <b>Warning:</b> The delete() method is not supported. Call the
 * method on the nested indexer instead.<p>
 */

public class DefaultIndexerManager implements IndexerIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(DefaultIndexerManager.class.getName());
  
  protected IndexerIF indexer;
  protected DocumentProcessorIF processor;

  /**
   * INTERNAL: Creates the manager and gives it the indexer used to do
   * the actual indexing.
   */
  public DefaultIndexerManager(IndexerIF indexer) {
    this.indexer = indexer;
  }

  /**
   * INTERNAL: Gets the document processor used by the indexer manager.
   */
  public DocumentProcessorIF getDocumentProcessor() {
    return processor;
  }

  /**
   * INTERNAL: Sets the document processor which is to be used by the
   * indexer manager.
   */
  public void setDocumentProcessor(DocumentProcessorIF processor) {
    this.processor = processor;
  }

  /**
   * INTERNAL: Gets the nested indexer.
   */
  public IndexerIF getIndexer() {
    return indexer;
  }

  /**
   * INTERNAL: Sets the nested indexer that is to be used by the indexer
   * manager.
   */
  public void setIndexer(IndexerIF indexer) {
    this.indexer = indexer;
  }

  public void index(DocumentIF document) throws IOException {
    // Process document
    if (processor != null) {
      try {
        processor.process(document);
      } catch (IOException e) {
        throw e;
      } catch (Exception e) {
        throw new IOException(e.toString()); 
      }
    }
    // Index document
    indexer.index(document);
  }
  
  public void delete(String field, String value) throws IOException {
    indexer.delete(field, value);
  }

  public void flush() throws IOException {
    indexer.flush();
  }
  
  public void delete() throws IOException {
    throw new UnsupportedOperationException("IndexerIF.close() is not supported.");
  }

  public void close() throws IOException {
    // Flush indexer
    indexer.flush();
  }

}





