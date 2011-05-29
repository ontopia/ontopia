
package net.ontopia.topicmaps.query.impl.utils;

import java.util.List;

import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.parser.AbstractClause;
import net.ontopia.topicmaps.query.parser.OrClause;

/**
 * INTERNAL: Used for testing and timing of queries.
 */
public interface QueryTraceListenerIF {

  public void startQuery();

  public void endQuery();
  
  public void enter(BasicPredicateIF predicate, AbstractClause clause, 
                    QueryMatches input);

  public void enter(OrClause clause, QueryMatches input);

  public void enter(List branch);
  
  public void leave(QueryMatches result);

  public void leave(List branch);

  public void enterOrderBy();

  public void leaveOrderBy();
  
  public void enterSelect(QueryMatches result);

  public void leaveSelect(QueryMatches result);

  public void trace(String message);
  
}
