
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.impl.utils.QueryContext;
import net.ontopia.topicmaps.query.impl.utils.QueryOptimizer;
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * INTERNAL: Query component that is used to fully execute basic tolog
 * clauses. The operations <i>satify</i>, <i>reduce</i>,
 * <i>count</i>and <i>sort</i> are called.
 */

public class BasicQueryComponent implements QueryComponentIF {

  //! // Define a logging category.
  //! static Logger log = LoggerFactory.getLogger(BasicQueryComponent.class.getName());

  protected TologQuery query;
  protected List clauses;
  protected net.ontopia.topicmaps.query.impl.basic.QueryProcessor qproc;
  
  public BasicQueryComponent(TologQuery query, List clauses,
                             net.ontopia.topicmaps.query.impl.basic.QueryProcessor qproc) throws InvalidQueryException {
    this.query = query;
    this.qproc = qproc;

    // optimize clauses    
    this.clauses = QueryOptimizer.getOptimizer(query).optimize(clauses, new QueryContext(query));
  }
  
  public QueryMatches satisfy(QueryMatches matches, Map arguments)
    throws InvalidQueryException {

    //! if (log.isDebugEnabled()) log.debug("BEFORE: " + matches.dump());
    
    matches = qproc.satisfy(clauses, matches);

    //! if (log.isDebugEnabled()) log.debug("AFTER: " + matches.dump());
    
    matches = qproc.reduce(query, matches);
    matches = qproc.count(query, matches);
    qproc.sort(query, matches);
    return matches;
  }

  public String toString() {    
    return "BQC: " + clauses;
  }
  
}
