
package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'association' predicate.
 */
public class AssociationPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
 
  public AssociationPredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  public String getName() {
    return "association";
  }

  public String getSignature() {
    return "a";
  }

  public int getCost(boolean[] boundparams) {
    if (boundparams[0])
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
  }

  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int associx = matches.getIndex(arguments[0]);

    if (matches.data[0][associx] == null)
      return PredicateUtils.collectionToOne(matches,
                                            topicmap.getAssociations().toArray(),
                                            associx, associx,
                                            PredicateUtils.NO_OPERATION);
    else
      return PredicateUtils.filterClass(matches, associx, AssociationIF.class);
  }  
}
