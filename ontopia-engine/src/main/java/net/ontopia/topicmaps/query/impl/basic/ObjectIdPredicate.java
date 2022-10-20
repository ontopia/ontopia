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
  
  @Override
  public String getName() {
    return "object-id";
  }

  @Override
  public String getSignature() {
    return "x s";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    } else {
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    // Interpret and validate arguments
    int objix = matches.getIndex(arguments[0]);
    int idix = matches.getIndex(arguments[1]);    

    if (matches.bound(objix) && !matches.bound(idix)) {
      return PredicateUtils.objectToOne(matches, objix, idix, TMObjectIF.class,
                                        PredicateUtils.OBJECT_TO_ID);
    } else if (!matches.bound(objix) && matches.bound(idix)) {
      return PredicateUtils.objectToOne(matches, idix, objix, String.class,
                                        PredicateUtils.ID_TO_OBJECT);
    } else if (matches.bound(objix) && matches.bound(idix)) {
      return PredicateUtils.filter(matches, objix, idix, TMObjectIF.class, 
                                   String.class, PredicateUtils.FILTER_ID);
    } else {
      return PredicateUtils.collectionToOne(matches, PredicateUtils.getAllObjects(topicmap).toArray(),
                                            objix, idix, PredicateUtils.GENERATE_ID);
    }
  }
  
}
