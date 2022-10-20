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

import java.util.Objects;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;

/**
 * INTERNAL: Implements the 'coalesce' predicate.
 */
public class CoalescePredicate implements BasicPredicateIF {

  @Override
  public String getName() {
    return "coalesce";
  }

  @Override
  public String getSignature() {
    return ". . .+"; // must have at least three arguments
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

    // get column indexes
    int[] colindexes = new int[arguments.length];
    for (int i=0; i < arguments.length; i++) {
      colindexes[i] = matches.getIndex(arguments[i]);
    }

    final boolean isBound = matches.bound(colindexes[0]);

    QueryMatches result = new QueryMatches(matches);

    for (int ix = 0; ix <= matches.last; ix++) {
      for (int i=1; i < arguments.length; i++) {      
        Object coalescedValue = matches.data[ix][colindexes[i]];
        if (coalescedValue != null) {
          // if bound then compare and filter
          if (isBound && !Objects.equals(matches.data[ix][colindexes[0]], coalescedValue)) {
            break;
          }
        
          Object[] newRow = (Object[]) matches.data[ix].clone();
          
          // if not bound then set first argument
          if (!isBound) {
            newRow[colindexes[0]] = matches.data[ix][colindexes[i]];
          }
        
          if (result.last+1 == result.size) {
            result.increaseCapacity();
          }
          result.last++;
          result.data[result.last] = newRow;
          break;
        }
      }
    }

    return result;
  }
  
}
