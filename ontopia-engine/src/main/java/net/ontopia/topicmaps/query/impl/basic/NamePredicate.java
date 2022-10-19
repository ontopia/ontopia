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
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.utils.TopicStringifiers;

/**
 * INTERNAL: The implementation of the 'name(topic, name-string)' predicate.
 */
public class NamePredicate implements BasicPredicateIF {

  public NamePredicate() {
  }

  @Override
  public String getName() {
    return "name";
  }

  @Override
  public String getSignature() {
    return "t s";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else if (boundparams[0] && !boundparams[1])
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    else if (!boundparams[0] && boundparams[1])
      return PredicateDrivenCostEstimator.INFINITE_RESULT; // will fail
    else
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
  }

  @Override
  public QueryMatches satisfy(QueryMatches result, Object[] arguments)
    throws InvalidQueryException {

    int topicix = result.getIndex(arguments[0]);
    int valueix = result.getIndex(arguments[1]);
    if (result.data[0][topicix] == null)
      throw new InvalidQueryException("Topic argument to 'name' must be bound");
    if (result.data[0][valueix] != null)
      throw new InvalidQueryException("Value argument to 'name' must be unbound");
    
    
    for (int ix = 0; ix <= result.last; ix++) {
      String value = TopicStringifiers.toString((TopicIF) result.data[ix][topicix]);
      result.data[ix][valueix] = value;
    }
    
    return result;
  }
}
