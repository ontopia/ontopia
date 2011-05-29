
package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.StringifierIF;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: The implementation of the 'name(topic, name-string)' predicate.
 */
public class NamePredicate implements BasicPredicateIF {
  protected StringifierIF strify;

  public NamePredicate() {
    strify = TopicStringifiers.getDefaultStringifier();
  }

  public String getName() {
    return "name";
  }

  public String getSignature() {
    return "t s";
  }
  
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else if (boundparams[0] && !boundparams[1])
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    else if (!boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.INFINITE_RESULT; // will fail
    else
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
  }

  public QueryMatches satisfy(QueryMatches result, Object[] arguments)
    throws InvalidQueryException {

    int topicix = result.getIndex(arguments[0]);
    int valueix = result.getIndex(arguments[1]);
    if (result.data[0][topicix] == null)
      throw new InvalidQueryException("Topic argument to 'name' must be bound");
    if (result.data[0][valueix] != null)
      throw new InvalidQueryException("Value argument to 'name' must be unbound");
    
    
    for (int ix = 0; ix <= result.last; ix++) {
      String value = strify.toString(result.data[ix][topicix]);
      result.data[ix][valueix] = value;
    }
    
    return result;
  }
}
