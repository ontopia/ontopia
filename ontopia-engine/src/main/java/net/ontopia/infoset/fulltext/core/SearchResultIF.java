
package net.ontopia.infoset.fulltext.core;

import java.io.IOException;

/**
 * INTERNAL: A search result containing a list of ranked hits.<p>
 */

public interface SearchResultIF {

  /**
   * INTERNAL: Returns the document located at the given index.
   */
  public DocumentIF getDocument(int hit) throws IOException;

  /**
   * INTERNAL: Returns the score of the document located at the given index.
   */
  public float getScore(int hit) throws IOException;

  /**
   * INTERNAL: Returns the number of hits (documents) in the search result.
   */
  public int hits() throws IOException;
  
}
