
// $Id: TopicMapPredicate.java,v 1.5 2007/09/18 10:03:57 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'topicmap' predicate.
 */
public class TopicMapPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
 
  public TopicMapPredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  public String getName() {
    return "topicmap";
  }

  public String getSignature() {
    return "m";
  }
  
  public int getCost(boolean[] boundparams) {
    if (boundparams[0])
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
  }

  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int tmix = matches.getIndex(arguments[0]);

    if (!matches.bound(tmix))
      return fillIn(matches, tmix);
    else
      return PredicateUtils.filterClass(matches, tmix, TopicMapIF.class);
  }
  
  // internal

  protected QueryMatches fillIn(QueryMatches matches, int tmix) {
    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      
      Object[] newRow = (Object[]) matches.data[ix].clone();
      newRow[tmix] = topicmap;

      result.last++;
      if (result.last == result.size) 
        result.increaseCapacity();
      result.data[result.last] = newRow;
    }

    return result;
  }
  
}
