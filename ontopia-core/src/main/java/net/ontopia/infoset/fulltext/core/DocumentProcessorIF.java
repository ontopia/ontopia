// $Id: DocumentProcessorIF.java,v 1.6 2004/11/18 13:11:04 grove Exp $

package net.ontopia.infoset.fulltext.core;

/**
 * INTERNAL: Interface for processing a document. Implementations would
 * typically modify, add or remove fields.<p>
 */

public interface DocumentProcessorIF {

  /**
   * INTERNAL: Can be used to figure out if it is necessary to process
   * the document.<p>
   *
   * This method should be used to quickly decide whether or not the
   * document needs to be processed. Note that this method should
   * return quickly, since it would normally be executed serially.<p>
   *
   * @return Returns true if the document should be processed.
   */
  public boolean needsProcessing(DocumentIF document);

  /**
   * INTERNAL: Processes the specified document.
   */
  public void process(DocumentIF document) throws Exception;
  
}





