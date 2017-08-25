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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Special predicate used when a topic is used as a
 * predicate but the topic is neither an association type nor an
 * occurrence type. It is needed because creating a
 * DynamicAssociationPredicate or DynamicOccurrencePredicate assumes a
 * particular type of parameter (pair or non-pair), whereas this
 * predicate will not complain about that.
 */
public class DynamicFailurePredicate extends AbstractDynamicPredicate {

  public DynamicFailurePredicate() {
    super("<fail>");
  }
  
  public DynamicFailurePredicate(TopicIF type, LocatorIF base) {
    super(type, base);
  }
  
  @Override
  public String getSignature() {
    return ".+";
  }

  @Override
  public int getCost(boolean[] boundparams) {
    return PredicateDrivenCostEstimator.FAIL_RESULT;
  }
  
  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    // this predicate only does a single thing: it fails, regardless
    // of what its arguments are, since there are neither associations
    // nor occurrences of this type
    
    return new QueryMatches(matches);
    
  }
  
}
