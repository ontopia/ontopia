
package net.ontopia.topicmaps.query.impl.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'variant(topicname, variantname)' predicate.
 */
public class VariantPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
 
  public VariantPredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  public String getName() {
    return "variant";
  }
  
  public String getSignature() {
    return "b v";
  }
  
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else if (boundparams[0] && !boundparams[1])
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    else if (!boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    else
      return PredicateDrivenCostEstimator.BIG_RESULT;
  }

  // variant(topic-name, variant)
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int tnix = matches.getIndex(arguments[0]);
    int vnix = matches.getIndex(arguments[1]);    
    
    if (matches.bound(tnix) && matches.bound(vnix)) {
      
      Prefetcher.prefetch(topicmap, matches, vnix,
			  Prefetcher.VariantNameIF, 
			  Prefetcher.VariantNameIF_name, false);

      return PredicateUtils.filter(matches, vnix, tnix, 
                                   VariantNameIF.class, TopicNameIF.class,
                                   PredicateUtils.FILTER_VARIANT);
    } else if (matches.bound(tnix) && !matches.bound(vnix)) {

      Prefetcher.prefetch(topicmap, matches, tnix,
			  Prefetcher.TopicNameIF, 
			  Prefetcher.TopicNameIF_variants, false);

      return PredicateUtils.objectToMany(matches, tnix, vnix, TopicNameIF.class,
                                         PredicateUtils.TNAME_TO_VNAME, null);
    } else if (!matches.bound(tnix) && !matches.bound(vnix)) {
      
      Prefetcher.prefetch(topicmap, matches, vnix,
			  Prefetcher.TopicNameIF, 
			  Prefetcher.TopicNameIF_variants, false);

      return PredicateUtils.generateFromCollection(matches, tnix, vnix,
                                                   getAllTopicNames(),
                                                   PredicateUtils.GENERATE_VARIANTS);
    } else {

      Prefetcher.prefetch(topicmap, matches, vnix,
			  Prefetcher.VariantNameIF, 
			  Prefetcher.VariantNameIF_name, false);

      return PredicateUtils.objectToOne(matches, vnix, tnix, VariantNameIF.class,
                                        PredicateUtils.VNAME_TO_TNAME);
    }
  }

  // --- Internal

  protected Collection getAllTopicNames() {    
    Collection all = new ArrayList(topicmap.getTopics().size() * 2);
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      all.addAll(topic.getTopicNames());
    }
    return all;
  }
  
}
