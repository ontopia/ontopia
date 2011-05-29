
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.Map;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * INTERNAL: Query component that is used to perform the <i>sort</i>
 * operation for QueryMatches instances. The implementation calls
 * impl.basic.QueryProcessor.sort(TologQuery, QueryMatches) and
 * returns the input instance since sorting in this case is performed
 * inline.
 */

public class BasicSortComponent implements QueryComponentIF {

  protected TologQuery query;
  protected net.ontopia.topicmaps.query.impl.basic.QueryProcessor qproc;
  
  public BasicSortComponent(TologQuery query,
                             net.ontopia.topicmaps.query.impl.basic.QueryProcessor qproc) {
    this.query = query;
    this.qproc = qproc;
  }

  public QueryMatches satisfy(QueryMatches matches, Map arguments)
    throws InvalidQueryException {
    qproc.sort(query, matches);
    return matches;
  }
  
}
