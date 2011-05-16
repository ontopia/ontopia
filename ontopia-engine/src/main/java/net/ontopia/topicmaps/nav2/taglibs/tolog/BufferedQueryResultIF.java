
// $Id: BufferedQueryResultIF.java,v 1.3 2005/03/30 17:11:17 opland Exp $

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
