
package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the '=' predicate.
 */
public class EqualsPredicate implements BasicPredicateIF {

  public String getName() {
    return "=";
  }

  public String getSignature() {
    return ". ."; // FIXME: not *quite* accurate; one of them does need to be bound
  }
  
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else if (boundparams[0] && !boundparams[1])
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    else if (!boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    else
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
  }
  
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int arg1 = matches.getIndex(arguments[0]);
    int arg2 = matches.getIndex(arguments[1]);

    if (matches.bound(arg1) && matches.bound(arg2))
      return PredicateUtils.filter(matches, arg1, arg2, Object.class,
                                   Object.class, PredicateUtils.FILTER_EQUALS);
    else if (matches.bound(arg1) && !matches.bound(arg2))
      return PredicateUtils.objectToOne(matches, arg1, arg2, Object.class,
                                        PredicateUtils.EQUAL_TO_EQUAL);
    else if (!matches.bound(arg1) && matches.bound(arg2))
      return PredicateUtils.objectToOne(matches, arg2, arg1, Object.class,
                                        PredicateUtils.EQUAL_TO_EQUAL);
    else
      throw new InvalidQueryException("At least one argument to = must be bound");
  }
  
}
