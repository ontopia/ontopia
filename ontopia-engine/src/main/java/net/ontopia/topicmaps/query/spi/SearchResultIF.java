
package net.ontopia.topicmaps.query.spi;

/**
 * PUBLIC: Search result interfaced used by implementations of the
 * SearcherIF interface.<p>
 */
public interface SearchResultIF {
  
  /**
   * PUBLIC: Moves ahead to the next result. Returns true if
   * there were more results.
   */
  public boolean next();
  
  /**
   * PUBLIC: Gets the current result value.
   */
  public Object getValue();

  /**
   * PUBLIC: Gets the score for the current result value;
   */
  public float getScore();

  /**
   * PUBLIC: Closes the search result. This method will be called when
   * done with the search results, so that resources can be released.
   */
  public void close();
  
}





