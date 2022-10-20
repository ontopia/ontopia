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

package net.ontopia.topicmaps.query.spi;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;

/**
 * EXPERIMENTAL: Base predicate that provides a simple interface for
 * implementing predicate filters. For such a predicate to work all
 * arguments must be bound at the time when the predicate is
 * executed. See the ProcessPredicate if you need more flexibility.<p>
 *
 * @since 4.0
 */
public abstract class FilterPredicate extends JavaPredicate {

  @Override
  public final QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {
    // validate arguments
    PredicateSignature sign = PredicateSignature.getSignature(this);
    sign.verifyBound(matches, arguments, this);
    
    // do actual work
    int[] argindexes = new int[arguments.length];
    for (int i=0; i < arguments.length; i++) {
      argindexes[i] = matches.getIndex(arguments[i]); 
      if (!matches.bound(argindexes[i])) {
        throw new InvalidQueryException("Argument " + arguments[i] + " to " + getName() + " must " +
                                        "be bound");
      }
    }
    
    // filter query matches
    QueryMatches result = new QueryMatches(matches);

    Object[] row = new Object[arguments.length];
    for (int ix = 0; ix <= matches.last; ix++) {
      
      for (int i=0; i < argindexes.length; i++) {
        row[i] = matches.data[ix][argindexes[i]];
      }
      
      if (filter(row)) {
        if (result.last+1 == result.size) {
          result.increaseCapacity();
        }
        result.last++;
        
        Object[] newRow = (Object[]) matches.data[ix].clone();
        result.data[result.last] = newRow;
      }
    }

    return result;
  }

  /**
   * EXPERIMENTAL: Returns true if the given row objects should be
   * included in the result.
   */
  public abstract boolean filter(Object[] row) throws InvalidQueryException;
  
}
