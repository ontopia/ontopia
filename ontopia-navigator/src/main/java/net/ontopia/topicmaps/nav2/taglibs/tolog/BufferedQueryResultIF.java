
package net.ontopia.topicmaps.nav2.taglibs.tolog;
import net.ontopia.topicmaps.query.core.QueryResultIF;

public interface BufferedQueryResultIF extends QueryResultIF {

  /** 
    * Bring this BufferedQueryResultIF back to the initial state after
    * it was created (instantiated to an implementing class).
    */
  public void restart();
  
  /**
    * Get the query of the query result.
    */
  public String getQuery();
}
