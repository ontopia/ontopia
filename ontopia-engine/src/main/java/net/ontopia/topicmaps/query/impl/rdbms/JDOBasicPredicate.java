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

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: A predicate wrapper that delegates all it's method calls
 * to the nested basic predicate.
 */
public class JDOBasicPredicate implements JDOPredicateIF {

  protected BasicPredicateIF pred;

  public JDOBasicPredicate(BasicPredicateIF pred) {
    this.pred = pred;
  }

  // --- PredicateIF implementation

  @Override
  public String getName() {
    return pred.getName();
  }

  @Override
  public String getSignature() throws InvalidQueryException {
    return pred.getSignature();
  }

  @Override
  public int getCost(boolean[] boundparams) {
    return pred.getCost(boundparams);
  }

  // --- BasicPredicateIF implementation

  @Override
  public QueryMatches satisfy(QueryMatches result, Object[] arguments)
    throws InvalidQueryException {
    return pred.satisfy(result, arguments);
  }

  // --- JDOPredicateIF implementation

  @Override
  public boolean isRecursive() {
    return false;
  }

  @Override
  public void prescan(QueryBuilder builder, List arguments) {
    // no-op
  }

  @Override
  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {
    // this predicate should be executed through basic predicate
    return false;
  }

  
}
