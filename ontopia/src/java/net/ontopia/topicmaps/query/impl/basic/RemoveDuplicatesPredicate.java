
// $Id: RemoveDuplicatesPredicate.java,v 1.5 2007/09/18 10:03:56 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'remove-duplicates()' predicate.
 */
public class RemoveDuplicatesPredicate implements BasicPredicateIF {
  private static final int CUTOFF = 100;
  
  public String getName() {
    return "remove-duplicates";
  }

  public String getSignature() {
    return "";
  }
  
  public int getCost(boolean[] boundparams) {
    return PredicateDrivenCostEstimator.FILTER_RESULT;
  }

  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    // don't bother with duplicates if we don't have a lot of matches
    if (matches.last < CUTOFF)
      return matches;
    
    return matches.removeDuplicates();
  }
  
}
