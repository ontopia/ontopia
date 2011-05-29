
package net.ontopia.topicmaps.query.impl.utils;

import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.PredicateClause;
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * INTERNAL: Implemented by classes that know how to optimize tolog queries.
 */
public interface QueryOptimizerIF {

  public void optimize(TologQuery query, QueryContext context)
    throws InvalidQueryException;
  
  public PredicateClause optimize(PredicateClause clause, QueryContext context)
    throws InvalidQueryException;

  public List optimize(List clauses, QueryContext context)
    throws InvalidQueryException;
  
}
