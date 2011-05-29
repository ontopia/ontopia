
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.Map;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * INTERNAL: Query component that is used to perform the <i>count</i>
 * operation for QueryMatches instances. The implementation returns
 * the result of a call to impl.basic.QueryProcessor.count(TologQuery,
 * QueryMatches).
 */

public class BasicCountComponent implements QueryComponentIF {

  protected TologQuery query;
  protected net.ontopia.topicmaps.query.impl.basic.QueryProcessor qproc;
  
  public BasicCountComponent(TologQuery query,
                             net.ontopia.topicmaps.query.impl.basic.QueryProcessor qproc) {
    this.query = query;
    this.qproc = qproc;
  }

  public QueryMatches satisfy(QueryMatches matches, Map arguments)
    throws InvalidQueryException {
    return qproc.count(query, matches);
  }
  
}
