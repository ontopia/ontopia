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
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the '<=' predicate.
 */
public class LessThanEqualsPredicate implements BasicPredicateIF {

  @Override
  public String getName() {
    return "<=";
  }

  @Override
  public String getSignature() {
    return ".! .!";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    return PredicateDrivenCostEstimator.getComparisonPredicateCost(boundparams);
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    PredicateSignature sign = PredicateSignature.getSignature(this);
    sign.verifyBound(matches, arguments, this);
    
    int colix1 = matches.getIndex(arguments[0]);
    int colix2 = matches.getIndex(arguments[1]);

    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      Object value1 = matches.data[ix][colix1];
      Object value2 = matches.data[ix][colix2];
      
      if (PredicateUtils.compare(value1, value2) > 0) {
        continue; // if greater than - skip it
      }
      
      if (result.last+1 == result.size) {
        result.increaseCapacity();
      }
      result.last++;

      // FIXME: is this really safe? or could row sharing give overwrites?
      result.data[result.last] = matches.data[ix];
    }

    return result;
  }
  
}
