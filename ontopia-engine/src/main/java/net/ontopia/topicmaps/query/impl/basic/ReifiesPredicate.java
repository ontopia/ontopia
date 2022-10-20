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
  
  @Override
  public String getName() {
    return "reifies";
  }

  @Override
  public String getSignature() {
    return "t marbvo";
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
      return PredicateDrivenCostEstimator.BIG_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int rrix = matches.getIndex(arguments[0]); // ReifieR
    int rdix = matches.getIndex(arguments[1]); // ReifieD

    if (!matches.bound(rdix) && matches.bound(rrix)) {
      // reifier to reified
      return PredicateUtils.objectToOne(matches, rrix, rdix, TopicIF.class,
              PredicateUtils.REIFIER_TO_REIFIED);
    } else if (matches.bound(rdix) && !matches.bound(rrix)) {
      // reified to reifier
      return PredicateUtils.objectToOne(matches, rdix, rrix, TMObjectIF.class,
              PredicateUtils.REIFIED_TO_REIFIER);
    } else if (matches.bound(rdix) && matches.bound(rrix)) {
      // filter out wrong
      return PredicateUtils.filter(matches, rdix, rrix, TMObjectIF.class,
              TopicIF.class, PredicateUtils.FILTER_REIFIES);
    } else {
      // completely open
      return PredicateUtils.collectionToOne(matches,
                                            topicmap.getTopics().toArray(),
                                            rrix, rdix, 
                                            PredicateUtils.GENERATE_REIFIES);
    }
  }
  
}
