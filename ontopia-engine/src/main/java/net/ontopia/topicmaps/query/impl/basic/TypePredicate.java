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
import java.util.List;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'type(typed, type)' predicate.
 */
public class TypePredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
  protected ClassInstanceIndexIF index;

  public TypePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    index = (ClassInstanceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
  }
  
  @Override
  public String getName() {
    return "type";
  }

  @Override
  public String getSignature() {
    return "arob t";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.BIG_RESULT;
    } else {
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int instix = matches.getIndex(arguments[0]);
    int typeix = matches.getIndex(arguments[1]);

    if (matches.data[0][typeix] != null && matches.data[0][instix] == null) {

      return typeToInstances(matches, arguments);

    } else if (matches.data[0][typeix] == null && matches.data[0][instix] != null) {
      
      prefetchType(matches, arguments, instix);
      return PredicateUtils.objectToOne(matches, instix, typeix, TypedIF.class,
                                        PredicateUtils.INSTANCE_TO_TYPE);

    } else if (matches.data[0][typeix] != null && matches.data[0][instix] != null) {

      prefetchType(matches, arguments, instix);
      return PredicateUtils.filter(matches, instix, typeix, TypedIF.class,
                                   TopicIF.class, PredicateUtils.FILTER_TYPE);
    } else {

      prefetchType(matches, arguments, instix);
      return PredicateUtils.collectionToOne(matches, getAllTyped(), instix, typeix,
                                            PredicateUtils.GENERATE_TYPE);
    }
  }
  
  protected void prefetchType(QueryMatches matches, Object[] arguments, int instix) {

    if (arguments[0] instanceof Variable) {
      String varname = ((Variable)arguments[0]).getName();
      Object[] types = matches.getQueryContext().getVariableTypes(varname);
      if (types != null) {
	for (int i=0; i < types.length; i++) {
	  if (OccurrenceIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, instix,
				Prefetcher.OccurrenceIF, 
				Prefetcher.OccurrenceIF_type, false);
	  } else if (AssociationRoleIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, instix,
				Prefetcher.AssociationRoleIF, 
				Prefetcher.AssociationRoleIF_type, false);
	  } else if (AssociationIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, instix,
				Prefetcher.AssociationIF, 
				Prefetcher.AssociationIF_type, false);
	  } else if (TopicNameIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, instix,
				Prefetcher.TopicNameIF, 
				Prefetcher.TopicNameIF_type, false);
	  }
	}
      }
    }
  }

  // internal

  private QueryMatches typeToInstances(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    QueryMatches result = new QueryMatches(matches);
    int instix = result.getIndex(arguments[0]);
    int typeix = result.getIndex(arguments[1]);

    for (int ix = 0; ix <= matches.last; ix++) {
      TopicIF topic = (TopicIF) matches.data[ix][typeix];

      List list = new ArrayList();
      list.addAll(index.getOccurrences(topic));
      list.addAll(index.getAssociations(topic));
      list.addAll(index.getAssociationRoles(topic));
      list.addAll(index.getTopicNames(topic));

      while (result.last + list.size() >= result.size) {
        result.increaseCapacity();
      }
      
      for (int inst = 0; inst < list.size(); inst++) {
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[instix] = list.get(inst);
        result.last++;
        result.data[result.last] = newRow;
      }
    }
    
    return result;
  }

  // --- Internal

  protected Object[] getAllTyped() {
    Collection typed = new ArrayList(topicmap.getTopics().size() * 5);

    Iterator it = index.getAssociationRoleTypes().iterator();
    while (it.hasNext()) {
      TopicIF type = (TopicIF) it.next();
      typed.addAll(index.getAssociationRoles(type));
    }

    it = index.getAssociationTypes().iterator();
    while (it.hasNext()) {
      TopicIF type = (TopicIF) it.next();
      typed.addAll(index.getAssociations(type));
    }

    it = index.getOccurrenceTypes().iterator();
    while (it.hasNext()) {
      TopicIF type = (TopicIF) it.next();
      typed.addAll(index.getOccurrences(type));
    }

    it = index.getTopicNameTypes().iterator();
    while (it.hasNext()) {
      TopicIF type = (TopicIF) it.next();
      typed.addAll(index.getTopicNames(type));
    }

    return typed.toArray();
  }
  
}
