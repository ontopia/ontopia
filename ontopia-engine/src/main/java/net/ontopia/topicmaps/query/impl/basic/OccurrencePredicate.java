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

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'occurrence' predicate.
 */
public class OccurrencePredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
 
  public OccurrencePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  @Override
  public String getName() {
    return "occurrence";
  }

  @Override
  public String getSignature() {
    return "t o";
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
    int occix = matches.getIndex(arguments[1]);

    if (matches.data[0][occix] == null && matches.data[0][topicix] != null) {

      Prefetcher.prefetch(topicmap, matches, topicix,
                          Prefetcher.TopicIF, 
                          Prefetcher.TopicIF_occurrences, false);

      return PredicateUtils.objectToMany(matches, topicix, occix, TopicIF.class,
                                         PredicateUtils.TOPIC_TO_OCCURRENCE, null);
      
    } else if (matches.data[0][occix] != null && matches.data[0][topicix] == null) {

      Prefetcher.prefetch(topicmap, matches, occix,
                          Prefetcher.OccurrenceIF, 
                          Prefetcher.OccurrenceIF_topic, false);

      return occurrenceToTopic(matches, topicix, occix);

    } else if (matches.data[0][occix] != null && matches.data[0][topicix] != null) {

      Prefetcher.prefetch(topicmap, matches, occix,
                          Prefetcher.OccurrenceIF, 
                          Prefetcher.OccurrenceIF_topic, false);

      /* using opposite order here of what the predicate accepts, because
         it's faster this way */
      return PredicateUtils.filter(matches, occix, topicix,
                                   OccurrenceIF.class, TopicIF.class,
                                   PredicateUtils.FILTER_OCCURRENCE);
    } else {
      
      Prefetcher.prefetch(topicmap, matches, topicix,
                          Prefetcher.TopicIF, 
                          Prefetcher.TopicIF_occurrences, false);
      
      return PredicateUtils.generateFromCollection(matches, topicix, occix,
                                                   topicmap.getTopics(),
                                                  PredicateUtils.GENERATE_OCCURRENCE);
    }
  }
  
  // internal

  private QueryMatches occurrenceToTopic(QueryMatches matches, int topicix, int occix) {

    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      if (!(matches.data[ix][occix] instanceof OccurrenceIF)) {
        continue;
      }

      if (result.last+1 == result.size) {
        result.increaseCapacity();
      }
      result.last++;
      
      Object[] newRow = (Object[]) matches.data[ix].clone();
      newRow[topicix] = ((OccurrenceIF) newRow[occix]).getTopic();
      result.data[result.last] = newRow;
    }

    return result;
  }
}
