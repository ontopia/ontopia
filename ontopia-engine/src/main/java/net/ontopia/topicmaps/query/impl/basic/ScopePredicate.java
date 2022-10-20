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

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'scope(scoped, theme)' predicate.
 */
public class ScopePredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
  protected ScopeIndexIF index;

  public ScopePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    index = (ScopeIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");
  }
  
  @Override
  public String getName() {
    return "scope";
  }

  @Override
  public String getSignature() {
    return "bvoa t";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.BIG_RESULT;
    } else {
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int scopedix = matches.getIndex(arguments[0]);
    int themeix = matches.getIndex(arguments[1]);

    if (!matches.bound(themeix) && matches.bound(scopedix)) {

      prefetchScope(matches, arguments, scopedix);
      return PredicateUtils.objectToMany(matches, scopedix, themeix,
                                         ScopedIF.class,
                                         PredicateUtils.SCOPED_TO_THEME, null);

    } else if (matches.bound(themeix) && matches.bound(scopedix)) {

      prefetchScope(matches, arguments, scopedix);
      return PredicateUtils.filter(matches, scopedix, themeix, ScopedIF.class,
                                   TopicIF.class, PredicateUtils.FILTER_SCOPE);

    } else if (matches.bound(themeix) && !matches.bound(scopedix)) {

      return PredicateUtils.objectToMany(matches, themeix, scopedix,
                                         TopicIF.class,
                                         PredicateUtils.THEME_TO_SCOPED, index);
    } else {

      prefetchScope(matches, arguments, scopedix);
      return PredicateUtils.generateFromCollection(matches, scopedix, themeix,
                                                   getAllScoped(),
                                                   PredicateUtils.GENERATE_SCOPED);
    }
  }
  
  protected void prefetchScope(QueryMatches matches, Object[] arguments, int scopedix) {

    if (arguments[0] instanceof Variable) {
      String varname = ((Variable)arguments[0]).getName();
      Object[] types = matches.getQueryContext().getVariableTypes(varname);
      if (types != null) {
	for (int i=0; i < types.length; i++) {
	  if (TopicNameIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, scopedix,
				Prefetcher.TopicNameIF, 
				Prefetcher.TopicNameIF_scope, false);
	  } else if (OccurrenceIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, scopedix,
				Prefetcher.OccurrenceIF, 
				Prefetcher.OccurrenceIF_scope, false);
	  } else if (VariantNameIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, scopedix,
				Prefetcher.VariantNameIF, 
				Prefetcher.VariantNameIF_scope, false);
	  } else if (AssociationIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, scopedix,
				Prefetcher.AssociationIF, 
				Prefetcher.AssociationIF_scope, false);
	  }
	}
      }
    }
  }

  // -- Internal

  protected Collection getAllScoped() {
    Collection scoped = new ArrayList(topicmap.getTopics().size());

    Iterator it = index.getAssociationThemes().iterator();
    while (it.hasNext()) {
      TopicIF theme = (TopicIF) it.next();
      scoped.addAll(index.getAssociations(theme));
    }

    it = index.getTopicNameThemes().iterator();
    while (it.hasNext()) {
      TopicIF theme = (TopicIF) it.next();
      scoped.addAll(index.getTopicNames(theme));
    }

    it = index.getVariantThemes().iterator();
    while (it.hasNext()) {
      TopicIF theme = (TopicIF) it.next();
      scoped.addAll(index.getVariants(theme));
    }
    
    it = index.getOccurrenceThemes().iterator();
    while (it.hasNext()) {
      TopicIF theme = (TopicIF) it.next();
      scoped.addAll(index.getOccurrences(theme));
    }

    return scoped;
  }
  
}
