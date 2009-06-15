
// $Id: QueryOptimizerIF.java,v 1.3 2005/03/21 18:27:31 larsga Exp $

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
