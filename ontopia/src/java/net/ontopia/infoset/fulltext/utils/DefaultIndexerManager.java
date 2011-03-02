// $Id: DefaultIndexerManager.java,v 1.11 2005/07/08 13:29:48 grove Exp $

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
  
  public int delete(String field, String value) throws IOException {
    return indexer.delete(field, value);
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





