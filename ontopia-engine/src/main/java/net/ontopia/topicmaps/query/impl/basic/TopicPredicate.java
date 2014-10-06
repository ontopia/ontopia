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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'type(typed, type)' predicate.
 */
public class TopicPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;

  public TopicPredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
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
