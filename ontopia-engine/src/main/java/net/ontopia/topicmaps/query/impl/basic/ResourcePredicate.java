/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.query.impl.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'resource' predicate.
 */
public class ResourcePredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
  protected NameIndexIF nindex;
  protected OccurrenceIndexIF oindex;

  public ResourcePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    this.nindex = (NameIndexIF) topicmap
      .getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");
    this.oindex = (OccurrenceIndexIF) topicmap
      .getIndex("net.ontopia.topicmaps.core.index.OccurrenceIndexIF");
  }
  
  @Override
  public String getName() {
    return "resource";
  }

  @Override
  public String getSignature() {
    return "ov s";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    } else {
      return PredicateDrivenCostEstimator.BIG_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int objix = matches.getIndex(arguments[0]);
    int resix = matches.getIndex(arguments[1]);

    if (matches.bound(objix) && !matches.bound(resix)) {

      prefetchResource(matches, arguments, objix);
      return PredicateUtils.objectToOne(matches, objix, resix, TMObjectIF.class,
                                        PredicateUtils.OBJECT_TO_RESOURCE);

    } else if (!matches.bound(objix) && matches.bound(resix)) {

      return PredicateUtils.objectToMany(matches, resix, objix, String.class,
                                         PredicateUtils.RESOURCE_TO_OBJECT, nindex, oindex);

    } else if (!matches.bound(objix) && !matches.bound(resix)) {

      prefetchResource(matches, arguments, objix);
      return PredicateUtils.collectionToOne(matches, getObjects(), objix, resix,
                                            PredicateUtils.GENERATE_RESOURCES);
    } else {

      prefetchResource(matches, arguments, objix);
      return PredicateUtils.filter(matches, objix, resix, TMObjectIF.class,
                                   String.class, PredicateUtils.FILTER_RESOURCE);
    }
  }
  
  protected void prefetchResource(QueryMatches matches, Object[] arguments, int objix) {

    if (arguments[0] instanceof Variable) {
      String varname = ((Variable)arguments[0]).getName();
      Object[] types = matches.getQueryContext().getVariableTypes(varname);
      if (types != null) {
	for (int i=0; i < types.length; i++) {
	  if (OccurrenceIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.OccurrenceIF, 
				Prefetcher.OccurrenceIF_locator, false);
	  } else if (VariantNameIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.VariantNameIF, 
				Prefetcher.VariantNameIF_locator, false);
	  }
	}
      }
    }
  }
  
  // --- Internal

  protected Object[] getObjects() {
    TopicIF[] topics = (TopicIF[]) topicmap.getTopics().toArray(new TopicIF[] {});
    Collection objects = new ArrayList(topics.length * 3);
    
    for (int tix = 0; tix < topics.length; tix++) {
      Collection bns = topics[tix].getTopicNames();
      objects.addAll(topics[tix].getOccurrences());
      
      Iterator it = bns.iterator();
      while (it.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it.next();
        objects.addAll(bn.getVariants());
      }
    }

    return objects.toArray();
  }
  
}
