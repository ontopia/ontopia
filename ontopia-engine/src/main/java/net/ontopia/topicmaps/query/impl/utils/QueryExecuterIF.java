
package net.ontopia.topicmaps.query.impl.utils;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * INTERNAL: This interface is implemented by QueryProcessorIFs. It
 * is only for internal use.
 */
public interface QueryExecuterIF {

  /**
   * INTERNAL: Executes the query, returning the results.
   */
  public QueryResultIF execute(TologQuery query)
    throws InvalidQueryException;
  
}





