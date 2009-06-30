
// $Id: SearcherIF.java,v 1.8 2005/07/07 13:15:08 grove Exp $

package net.ontopia.infoset.fulltext.core;

import java.io.IOException;

/**
 * INTERNAL: Represents a search engine. Instances of this class are
 * able to perform searches across a collection of documents.<p>
 */

public interface SearcherIF {
  
  /**
   * INTERNAL: Performs a query on an index. The actual query syntax is
   * search engine dependent.
   */
  public SearchResultIF search(String query) throws IOException;
  
  /**
   * INTERNAL: Releases resources associated with this searcher.
   */
  public void close() throws IOException;

}
