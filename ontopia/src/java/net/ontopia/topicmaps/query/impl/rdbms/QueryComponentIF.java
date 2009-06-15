// $Id: QueryComponentIF.java,v 1.6 2005/07/13 08:54:59 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.Map;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: Represents a tolog query component. A tolog query is
 * sometimes split into smaller component that performs operations on
 * an existing query matches (result sets).<p>
 *
 * The result of processing a component is another QueryMatches
 * instance that can be used as the input of another query components,
 * or be the result of the entire query.<p>
 */
public interface QueryComponentIF {

  /**
   * INTERNAL: Processes the specified QueryMatches instance and
   * produces a new QueryMatches instance based on information in the
   * input instance.
   */
  public QueryMatches satisfy(QueryMatches matches, Map arguments)
    throws InvalidQueryException;
  
}
