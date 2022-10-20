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

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'topicmap' predicate.
 */
public class TopicMapPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
 
  public TopicMapPredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  @Override
  public String getName() {
    return "topicmap";
  }

  @Override
  public String getSignature() {
    return "m";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else {
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int tmix = matches.getIndex(arguments[0]);

    if (!matches.bound(tmix)) {
      return fillIn(matches, tmix);
    } else {
      return PredicateUtils.filterClass(matches, tmix, TopicMapIF.class);
    }
  }
  
  // internal

  protected QueryMatches fillIn(QueryMatches matches, int tmix) {
    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      
      Object[] newRow = (Object[]) matches.data[ix].clone();
      newRow[tmix] = topicmap;

      result.last++;
      if (result.last == result.size) {
        result.increaseCapacity();
      }
      result.data[result.last] = newRow;
    }

    return result;
  }
  
}
