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

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'topic-name(topic, name)' predicate.
 */
public class TopicNamePredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
 
  public TopicNamePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  @Override
  public String getName() {
    return "topic-name";
  }

  @Override
  public String getSignature() {
    return "t b";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    } else {
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int topicix = matches.getIndex(arguments[0]);
    int nameix = matches.getIndex(arguments[1]);

    if (!matches.bound(nameix) && matches.bound(topicix)) {

      Prefetcher.prefetch(topicmap, matches, topicix,
                          Prefetcher.TopicIF, 
                          Prefetcher.TopicIF_names, false);
      
      return PredicateUtils.objectToMany(matches, topicix, nameix, TopicIF.class,
                                         PredicateUtils.TOPIC_TO_NAME, null);
      
    } else if (matches.bound(nameix) && !matches.bound(topicix)) {
      
      Prefetcher.prefetch(topicmap, matches, nameix,
                          Prefetcher.TopicNameIF, 
                          Prefetcher.TopicNameIF_topic, false);
      
      return PredicateUtils.objectToOne(matches, nameix, topicix, TopicNameIF.class,
                                        PredicateUtils.NAME_TO_TOPIC);
      
    } else if (matches.bound(nameix) && matches.bound(topicix)) {
      
      Prefetcher.prefetch(topicmap, matches, nameix,
                          Prefetcher.TopicNameIF, 
                          Prefetcher.TopicNameIF_topic, false);
      
      return PredicateUtils.filter(matches, nameix, topicix, TopicNameIF.class,
                                   TopicIF.class, PredicateUtils.FILTER_TOPIC_NAME);
    } else {
      
      Prefetcher.prefetch(topicmap, matches, topicix,
                          Prefetcher.TopicIF, 
                          Prefetcher.TopicIF_names, false);
      
      return PredicateUtils.generateFromCollection(matches, topicix, nameix,
                                                   topicmap.getTopics(),
                                                   PredicateUtils.GENERATE_TOPIC_NAME);
    }
  }
 
}
