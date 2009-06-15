// $Id: IndexerIF.java,v 1.10 2005/07/08 13:29:48 grove Exp $

package net.ontopia.infoset.fulltext.core;

import java.io.IOException;

/**
 * PUBLIC: Represents a search engine indexer. Instances of this class
 * are able to index documents and generate an index which can be used
 * for searching those documents.<p>
 */

public interface IndexerIF {

  /**
   * PUBLIC: Indexes the specified document. This includes tokenizing,
   * indexing and storing the document fields.
   */
  public void index(DocumentIF document) throws IOException;
  
  /**
   * PUBLIC: Removes all documents with the specified field value from
   * the index. This method should generally be, but is not limited
   * to, used to delete documents by their identity field.<p>
   *
   * @return The number of documents that was deleted from the
   * index. -1 is return if this number is unknown.
   */
  public int delete(String field, String value) throws IOException;
    
  /**
   * PUBLIC: Flushes all changes done to the index. A flushing
   * operation can include actions like persisting changes and
   * optimizing the index.
   */
  public void flush() throws IOException;
  
  /**
   * PUBLIC: Deletes the index. The indexer is closed after this
   * method returns. Note that some indexers might not support this
   * operation.
   *
   * @since 1.3
   */
  public void delete() throws IOException;

  /**
   * PUBLIC: Closes the indexer. After the indexer has been closed it
   * cannot (generally) be reopened.
   */
  public void close() throws IOException;
  
}
