
// $Id: JDOBasicPredicate.java,v 1.4 2007/09/18 10:04:02 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: A predicate wrapper that delegates all it's method calls
 * to the nested basic predicate.
 */
public class JDOBasicPredicate implements JDOPredicateIF {

  protected BasicPredicateIF pred;

  public JDOBasicPredicate(BasicPredicateIF pred) {
    this.pred = pred;
  }

  // --- PredicateIF implementation

  public String getName() {
    return pred.getName();
  }

  public String getSignature() throws InvalidQueryException {
    return pred.getSignature();
  }

  public int getCost(boolean[] boundparams) {
    return pred.getCost(boundparams);
  }

  // --- BasicPredicateIF implementation

  public QueryMatches satisfy(QueryMatches result, Object[] arguments)
    throws InvalidQueryException {
    return pred.satisfy(result, arguments);
  }

  // --- JDOPredicateIF implementation

  public boolean isRecursive() {
    return false;
  }

  public void prescan(QueryBuilder builder, List arguments) {
  }

  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {
    // this predicate should be executed through basic predicate
    return false;
  }

  
}
