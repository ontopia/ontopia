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

import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.AbstractClause;
import net.ontopia.topicmaps.query.parser.NotClause;
import net.ontopia.topicmaps.query.parser.OrClause;
import net.ontopia.topicmaps.query.parser.ParsedRule;
import net.ontopia.topicmaps.query.parser.PredicateClause;

/**
 * INTERNAL: Implements rule predicates.
 */
public class RulePredicate
  extends net.ontopia.topicmaps.query.impl.basic.RulePredicate
  implements JDOPredicateIF {

  public RulePredicate(ParsedRule rule) {
    super(rule);
  }
  
  // --- JDOPredicateIF implementation

  @Override
  public boolean isRecursive() {
    return true;
  }

  @Override
  public void prescan(QueryBuilder builder, List arguments) {
    // no-op
  }
  
  @Override
  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {
    // TODO: Rule predicates does not yet support JDO expressions.
    return false;
  }

  // --- Misc.
  
  public boolean isSelfRecursive() {
    return isRecursive(rule.getClauses(), this);
  }
  
  protected boolean isRecursive(List clauses, RulePredicate relative_to) {
    Iterator iter = clauses.iterator();
    while (iter.hasNext()) {
      AbstractClause clause = (AbstractClause)iter.next();
      if (clause instanceof PredicateClause) {
        PredicateClause _clause = (PredicateClause)clause;        
        JDOPredicateIF pred = (JDOPredicateIF)_clause.getPredicate();
        
        if (pred instanceof RulePredicate) {
          // Check to see if rule predicate is recursive
          RulePredicate rule_pred = (RulePredicate)pred;
          if (rule_pred.equals(relative_to) ||
              rule_pred.isRecursive(rule_pred.getClauses(), relative_to)) {
            return true;
          }
        }
      } else if (clause instanceof OrClause) {
        Iterator iter2 = ((OrClause)clause).getAlternatives().iterator();
        while (iter2.hasNext()) {
          if (isRecursive((List)iter2.next(), relative_to)) {
            return true;
          }
        }
      } else if (clause instanceof NotClause) {          
        if (isRecursive(((NotClause)clause).getClauses(), relative_to)) {
          return true;
        }        
      }      
    }
    return false;
  }
  
}
