
// $Id: ReifiesPredicate.java,v 1.13 2007/09/18 10:03:56 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'reifies(reifier, reified)' predicate.
 */
public class ReifiesPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
 
  public ReifiesPredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  public String getName() {
    return "reifies";
  }

  public String getSignature() {
    return "t x";
  }
  
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else if (boundparams[0] && !boundparams[1])
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    else if (!boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else
      return PredicateDrivenCostEstimator.BIG_RESULT;
  }

  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int rrix = matches.getIndex(arguments[0]); // ReifieR
    int rdix = matches.getIndex(arguments[1]); // ReifieD

    if (!matches.bound(rdix) && matches.bound(rrix))
      // reifier to reified
      return PredicateUtils.objectToOne(matches, rrix, rdix, TopicIF.class,
                                        PredicateUtils.REIFIER_TO_REIFIED);
    else if (matches.bound(rdix) && !matches.bound(rrix))
      // reified to reifier
      return PredicateUtils.objectToOne(matches, rdix, rrix, TMObjectIF.class,
                                        PredicateUtils.REIFIED_TO_REIFIER);
    else if (matches.bound(rdix) && matches.bound(rrix))
      // filter out wrong
      return PredicateUtils.filter(matches, rdix, rrix, TMObjectIF.class,
                                   TopicIF.class, PredicateUtils.FILTER_REIFIES);
    else
      // completely open
      return PredicateUtils.collectionToOne(matches,
                                            topicmap.getTopics().toArray(),
                                            rrix, rdix, 
                                            PredicateUtils.GENERATE_REIFIES);
  }
  
}
