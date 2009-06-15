
// $Id: ObjectIdPredicate.java,v 1.3 2007/09/18 10:03:56 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'object(thing, id)' predicate.
 */
public class ObjectIdPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;

  public ObjectIdPredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  public String getName() {
    return "object-id";
  }

  public String getSignature() {
    return "x s";
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

    // Interpret and validate arguments
    int objix = matches.getIndex(arguments[0]);
    int idix = matches.getIndex(arguments[1]);    

    if (matches.bound(objix) && !matches.bound(idix))
      return PredicateUtils.objectToOne(matches, objix, idix, TMObjectIF.class,
                                        PredicateUtils.OBJECT_TO_ID);
    else if (!matches.bound(objix) && matches.bound(idix))
      return PredicateUtils.objectToOne(matches, idix, objix, String.class,
                                        PredicateUtils.ID_TO_OBJECT);
    else if (matches.bound(objix) && matches.bound(idix))
      return PredicateUtils.filter(matches, objix, idix, TMObjectIF.class, 
                                   String.class, PredicateUtils.FILTER_ID);
    else
      return PredicateUtils.collectionToOne(matches, PredicateUtils.getAllObjects(topicmap).toArray(),
                                            objix, idix, PredicateUtils.GENERATE_ID);
  }
  
}
