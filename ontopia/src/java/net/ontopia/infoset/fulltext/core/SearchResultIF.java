
// $Id: SearchResultIF.java,v 1.8 2003/10/30 09:48:08 grove Exp $

package net.ontopia.infoset.fulltext.core;

import java.io.IOException;

/**
 * PUBLIC: A search result containing a list of ranked hits.<p>
 */

public interface SearchResultIF {

  /**
   * PUBLIC: Returns the document located at the given index.
   */
  public DocumentIF getDocument(int hit) throws IOException;

  /**
   * PUBLIC: Returns the score of the document located at the given index.
   */
  public float getScore(int hit) throws IOException;

  /**
   * PUBLIC: Returns the number of hits (documents) in the search result.
   */
  public int hits() throws IOException;
  
}
