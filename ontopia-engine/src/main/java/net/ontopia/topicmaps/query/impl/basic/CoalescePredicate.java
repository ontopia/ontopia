
package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.utils.ObjectUtils;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'coalesce' predicate.
 */
public class CoalescePredicate implements BasicPredicateIF {

  public String getName() {
    return "coalesce";
  }

  public String getSignature() {
    return ". . .+"; // must have at least three arguments
  }
  
  public int getCost(boolean[] boundparams) {
    return PredicateDrivenCostEstimator.getComparisonPredicateCost(boundparams);
  }

  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    PredicateSignature sign = PredicateSignature.getSignature(this);
    sign.verifyBound(matches, arguments, this);

    // get column indexes
    int[] colindexes = new int[arguments.length];
    for (int i=0; i < arguments.length; i++) {
      colindexes[i] = matches.getIndex(arguments[i]);
    }

    final boolean isBound = matches.bound(colindexes[0]);

    QueryMatches result = new QueryMatches(matches);

    for (int ix = 0; ix <= matches.last; ix++) {
      for (int i=1; i < arguments.length; i++) {      
        Object coalescedValue = matches.data[ix][colindexes[i]];
        if (coalescedValue != null) {
          // if bound then compare and filter
          if (isBound && ObjectUtils.different(matches.data[ix][colindexes[0]], coalescedValue))
            break;
        
          Object[] newRow = (Object[]) matches.data[ix].clone();
          
          // if not bound then set first argument
          if (!isBound)
            newRow[colindexes[0]] = matches.data[ix][colindexes[i]];
        
          if (result.last+1 == result.size) 
            result.increaseCapacity();
          result.last++;
          result.data[result.last] = newRow;
          break;
        }
      }
    }

    return result;
  }
  
}
