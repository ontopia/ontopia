
package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'item-identifier(thing, locator)' predicate.
 */
public class ItemIdentifierPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
  protected String predicateName;

  public ItemIdentifierPredicate(TopicMapIF topicmap, String predicateName) {
    this.topicmap = topicmap;
    this.predicateName = predicateName;
  }
  
  public String getName() {
    return predicateName;
  }

  public String getSignature() {
    return "x s";
  }
  
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else if (boundparams[0] && !boundparams[1])
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    else if (!boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    else
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
  }

  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    // Interpret and validate arguments
    int objix = matches.getIndex(arguments[0]);
    int locix = matches.getIndex(arguments[1]);    

    if (matches.bound(objix) && !matches.bound(locix)) {

      prefetchSources(matches, arguments, objix);
      return PredicateUtils.objectToMany(matches, objix, locix, TMObjectIF.class,
                                         PredicateUtils.OBJECT_TO_SRCLOC, null);

    } else if (!matches.bound(objix) && matches.bound(locix)) {

      return PredicateUtils.objectToOne(matches, locix, objix, String.class,
                                        PredicateUtils.SRCLOC_TO_OBJECT);

    } else if (matches.bound(objix) && matches.bound(locix)) {

      prefetchSources(matches, arguments, objix);
      return PredicateUtils.filter(matches, objix, locix, TMObjectIF.class, 
                                   String.class, PredicateUtils.FILTER_SRCLOC);

    } else {

      prefetchSources(matches, arguments, objix);
      return PredicateUtils.generateFromCollection(matches, objix, locix,
                                                   PredicateUtils.getAllObjects(topicmap),
                                                   PredicateUtils.GENERATE_SRCLOC);
    }
  }
  
  protected void prefetchSources(QueryMatches matches, Object[] arguments, int objix) {
    
    if (arguments[0] instanceof Variable) {
      String varname = ((Variable)arguments[0]).getName();
      Object[] types = matches.getQueryContext().getVariableTypes(varname);
      if (types != null) {
	for (int i=0; i < types.length; i++) {
	  if (TopicIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.TopicIF, 
				Prefetcher.TopicIF_sources, false);
	  } else if (TopicNameIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.TopicNameIF, 
				Prefetcher.TopicNameIF_sources, false);
	  } else if (AssociationIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.AssociationIF, 
				Prefetcher.AssociationIF_sources, false);
	  } else if (OccurrenceIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.OccurrenceIF, 
				Prefetcher.OccurrenceIF_sources, false);
	  } else if (AssociationRoleIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.AssociationRoleIF, 
				Prefetcher.AssociationRoleIF_sources, false);
	  } else if (VariantNameIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.VariantNameIF, 
				Prefetcher.VariantNameIF_sources, false);
	  }
	}
      }
    }
  }
  
}
