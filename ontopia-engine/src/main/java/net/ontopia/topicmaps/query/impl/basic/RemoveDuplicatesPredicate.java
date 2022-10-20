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

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'remove-duplicates()' predicate.
 */
public class RemoveDuplicatesPredicate implements BasicPredicateIF {
  private boolean first;
  private static final int CUTOFF = 100;

  public RemoveDuplicatesPredicate(boolean first) {
    // the optimizer inserts two duplicate removal predicates: one at
    // the start of the predicate list (this has first=true), and one
    // at the end (which has first=false).
    this.first = first;
  }
  
  @Override
  public String getName() {
    return "remove-duplicates";
  }

  @Override
  public String getSignature() {
    return "";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (first) {
      // optimizer puts us first
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else {
      // optimizer puts us last
      return PredicateDrivenCostEstimator.INFINITE_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    // don't bother with duplicates if we don't have a lot of matches
    if (matches.last < CUTOFF) {
      return matches;
    }
    
    return matches.removeDuplicates();
  }
  
}
