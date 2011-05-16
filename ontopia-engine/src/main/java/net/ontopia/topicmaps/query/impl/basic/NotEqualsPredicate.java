
// $Id: NotEqualsPredicate.java,v 1.20 2007/09/18 10:03:56 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the '/=' predicate.
 */
public class NotEqualsPredicate implements BasicPredicateIF {

  public String getName() {
    return "/=";
  }

  public String getSignature() {
    return ".! .!";
  }
  
  public int getCost(boolean[] boundparams) {
    return PredicateDrivenCostEstimator.getComparisonPredicateCost(boundparams);
  }

  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    PredicateSignature sign = PredicateSignature.getSignature(this);
    sign.verifyBound(matches, arguments, this);
    
    int colix1 = matches.getIndex(arguments[0]);
    int colix2 = matches.getIndex(arguments[1]);

    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      Object value1 = matches.data[ix][colix1];
      Object value2 = matches.data[ix][colix2];

      if ((value1 == null && value2 == null) ||
          (value1 != null && value2 != null && value1.equals(value2)))
        continue; // not a match, so skip it
      
      if (result.last+1 == result.size) 
        result.increaseCapacity();
      result.last++;

      // FIXME: is this really safe? or could row sharing give overwrites?
      result.data[result.last] = matches.data[ix];
    }

    return result;
  }
  
}
