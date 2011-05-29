
package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'type(typed, type)' predicate.
 */
public class TopicPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
  protected ClassInstanceIndexIF index;

  public TopicPredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    index = (ClassInstanceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
  }
  
  public String getName() {
    return "topic";
  }

  public String getSignature() {
    return "t";
  }
  
  public int getCost(boolean[] boundparams) {
    if (boundparams[0])
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
  }

  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int topicix = matches.getIndex(arguments[0]);
    
    if (matches.data[0][topicix] == null)
      return PredicateUtils.collectionToOne(matches, topicmap.getTopics().toArray(),
                                            topicix, topicix,
                                            PredicateUtils.NO_OPERATION);
    else
      return PredicateUtils.filterClass(matches, topicix, TopicIF.class);
  }
  
}
