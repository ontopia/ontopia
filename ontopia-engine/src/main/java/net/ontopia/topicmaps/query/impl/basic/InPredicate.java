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

import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'in(var, e1, ..., eN)' predicate.
 */
public class InPredicate implements BasicPredicateIF {

  @Override
  public String getName() {
    return "in";
  }

  @Override
  public String getSignature() {
    return ". .+";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else {
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    // Create copy of arguments array
    Set values = new HashSet(arguments.length-1);
    for (int i=1; i < arguments.length; i++) {
      values.add(arguments[i]);
    }
    
    int varix = matches.getIndex(arguments[0]);
    
    if (matches.data[0][varix] != null) {
      return filter(matches, varix, values);
    } else {
      return PredicateUtils.collectionToOne(matches, values.toArray(),
                                            varix, varix,
                                            PredicateUtils.NO_OPERATION);
    }
  }
  
  protected QueryMatches filter(QueryMatches matches, int ix1, Set values) {
    QueryMatches result = new QueryMatches(matches);
    
    int nextix = 0;
    for (int ix = 0; ix <= matches.last; ix++) {
      Object object = matches.data[ix][ix1];
      
      // check value found against value given
      if (object == null || !values.contains(object)) {
        continue;
      }
      
      // ok, add match
      result.data[nextix++] = matches.data[ix];
    }

    result.last = nextix - 1;
    return result;
  }
  
}
