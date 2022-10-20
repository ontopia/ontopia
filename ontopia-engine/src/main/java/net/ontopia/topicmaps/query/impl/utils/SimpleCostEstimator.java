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

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Set;
import java.util.List;
import java.util.Iterator;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.topicmaps.query.parser.OrClause;
import net.ontopia.topicmaps.query.parser.NotClause;
import net.ontopia.topicmaps.query.parser.PredicateIF;
import net.ontopia.topicmaps.query.parser.AbstractClause;
import net.ontopia.topicmaps.query.parser.PredicateClause;
import net.ontopia.topicmaps.impl.utils.Argument;

public class SimpleCostEstimator extends CostEstimator {
  /**
   * INTERNAL: Computes the cost of evaluating the given clause
   * in the given context of variable bindings. The cost largely
   * depends on the number of unbound variables in the clause.
   * @param context A set of bound variables.
   * @param clause The clause whose cost we want to compute.
   * @param literalvars Contains the variables representing literals.
   *                    Only an issue in rules.
   * @param rulename The name of the current rule (so we can delay
   *                 recursive evaluation).
   */
  @Override
  public int computeCost(Set context, AbstractClause clause,
                         Set literalvars, String rulename) {
    int cost = 0;

    // penalties for unbound variables
    Iterator it = clause.getAllVariables().iterator();
    while (it.hasNext()) {
      Object variable = it.next();
      if (!context.contains(variable)) {
        cost += 10;
      }
      if (literalvars.contains(variable)) {
        cost += 1; // this is really a literal...
      }

      // penalties and bonuses for various kinds of predicates
      if (clause instanceof PredicateClause) {
        // initialize
        PredicateIF predicate = ((PredicateClause) clause).getPredicate();
        PredicateSignature sign = null;
        try {
          sign = PredicateSignature.getSignature(predicate);
        } catch (InvalidQueryException e) {
          throw new OntopiaRuntimeException(e);
        }

        // check for specific predicates (FIXME: generalize!)
        String name = predicate.getName();
        if ("instance-of".equals(name)) {
          cost += 1;
        } else if ("/=".equals(name)) {
          cost -= 5;
        } else if ("value-like".equals(name)) {
          cost -= 11; // value-like must go first
        } else if (rulename.equals(name)) {
          cost += 100; // recursive evaluation should happen late
        }

        // check for arguments which must be bound
        Argument lastArgument = null;

        List realargs = clause.getArguments();
        for (int ix = 0; ix < realargs.size(); ix++) {
          Argument argument = sign.getArgument(ix);          

          Object arg = realargs.get(ix);
          if (arg instanceof Variable) {
            if (!context.contains(arg) &&
                (argument == null ?
                 (lastArgument == null ? false :
                  (lastArgument.isRepeatable() && lastArgument.mustBeBound())) : // take repeatable arguments into account
                 argument.mustBeBound())) {
              cost += 100000; // can't run this one now
            }
          }
          
          if (argument != null) {
            lastArgument = argument;
          }
        }
      } else if (clause instanceof NotClause) {
        cost += 100; // not clauses must be done late
      } else if (clause instanceof OrClause &&
               ((OrClause) clause).getAlternatives().size() == 1) {
        cost += 50; // optional clauses should also be done late
      }
      
    }

    // penalties for starting from literals
    // NEW: this bit is somewhat untried
    // FIXME: this appears to also penalize parameters!!
    cost += clause.getAllLiterals().size(); // that is, 1 point per literal
    return cost;
  }
}
