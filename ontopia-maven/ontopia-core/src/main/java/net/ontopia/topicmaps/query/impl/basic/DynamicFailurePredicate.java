
// $Id: DynamicFailurePredicate.java,v 1.6 2007/09/18 10:03:55 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Special predicate used when a topic is used as a
 * predicate but the topic is neither an association type nor an
 * occurrence type. It is needed because creating a
 * DynamicAssociationPredicate or DynamicOccurrencePredicate assumes a
 * particular type of parameter (pair or non-pair), whereas this
 * predicate will not complain about that.
 */
public class DynamicFailurePredicate extends AbstractDynamicPredicate {

  public DynamicFailurePredicate() {
    super("<fail>");
  }
  
  public DynamicFailurePredicate(TopicIF type, LocatorIF base) {
    super(type, base);
  }
  
  public String getSignature() {
    return ".+";
  }

  public int getCost(boolean[] boundparams) {
    return PredicateDrivenCostEstimator.FAIL_RESULT;
  }
  
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    // this predicate only does a single thing: it fails, regardless
    // of what its arguments are, since there are neither associations
    // nor occurrences of this type
    
    return new QueryMatches(matches);
    
  }
  
}
