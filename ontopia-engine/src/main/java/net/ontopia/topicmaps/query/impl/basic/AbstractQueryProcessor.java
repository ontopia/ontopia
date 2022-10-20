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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.AbstractClause;
import net.ontopia.topicmaps.query.parser.NotClause;
import net.ontopia.topicmaps.query.parser.OrClause;
import net.ontopia.topicmaps.query.parser.Pair;
import net.ontopia.topicmaps.query.parser.Parameter;
import net.ontopia.topicmaps.query.parser.PredicateClause;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: A collection of utility methods used by classes which need
 * to evaluate queries. Simply a code-sharing class for QueryProcessor
 * and RulePredicate.
 */
public abstract class AbstractQueryProcessor {

  private static BasicPredicateIF NOT_PREDICATE = new NotPredicate();

  /**
   * INTERNAL: Finds all the values (constants and variables) used in
   * the set of clauses and returns them in a collection with no
   * duplicates.
   */
  public Collection findClauseItems(List clauses, Map parameters) {
    // find the set of constants and variables used in the clauses
    Collection items = new ArrayList();
    Iterator it = clauses.iterator();
    while (it.hasNext()) {
      AbstractClause clause = (AbstractClause) it.next();
      Iterator it2 = clause.getArguments().iterator();
      while (it2.hasNext()) {
        Object argument = it2.next();
        if (argument instanceof Pair) { 
          // WARN: this means that second item cannot be bound.
          argument = ((Pair) argument).getFirst();
        }
        if (argument instanceof Parameter) {
          String pname = ((Parameter) argument).getName();
          argument = parameters.get(pname);
        }
        if (!items.contains(argument)) {
          items.add(argument);
        }
      }
    }

    return items;
  }

  /**
   * INTERNAL: Finds all the variables used in the set of clauses
   * and returns them in a collection with no duplicates.
   */
  public Collection findClauseVariables(List clauses) {
    // find the set of variables used in the clauses
    Collection items = new ArrayList();
    Iterator it = clauses.iterator();
    while (it.hasNext()) {
      AbstractClause clause = (AbstractClause) it.next();
      Iterator it2 = clause.getArguments().iterator();
      while (it2.hasNext()) {
        Object argument = it2.next();
        if (argument instanceof Pair) { 
          // WARN: this means that second item cannot be bound.
          argument = ((Pair) argument).getFirst();
        }
        
        if (argument instanceof Variable && !items.contains(argument)) {
          items.add(argument);
        }
      }
    }

    return items;
  }
  
  /**
   * INTERNAL: Takes the query parameters and produces the complete
   * list of matches. It's static because it's not inherited, it uses
   * no instance variables, and this makes it easier to access from
   * outside when needed.
   */
  public static QueryMatches satisfy(List clauses, QueryMatches result)
    throws InvalidQueryException {
    // WARNING: method used by rdbms tolog

    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause theClause = (AbstractClause) clauses.get(ix);

      if (theClause instanceof PredicateClause) {
        
        // check to see if thread has been interrupted
        if(Thread.currentThread().isInterrupted()) {
          throw new OntopiaRuntimeException(new InterruptedException());
        }

        // execute predicate
        PredicateClause clause = (PredicateClause) theClause;
        BasicPredicateIF predicate = (BasicPredicateIF) clause.getPredicate();
        QueryTracer.enter(predicate, clause, result);
        Object[] argarr = makeArgumentArray(clause, result.getQueryContext());
        result = predicate.satisfy(result, argarr);
        QueryTracer.leave(result);

      } else if (theClause instanceof OrClause) {
        OrClause clause = (OrClause) theClause;
        QueryMatches matches = new QueryMatches(result);
        QueryTracer.enter(clause, result);

        if (clause.getShortCircuit()) {
          // shortcircuting OR
          Iterator it = clause.getAlternatives().iterator();
          while (it.hasNext()) {
            List branch = (List) it.next();
            QueryTracer.enter(branch);
            QueryMatches _matches = satisfy(branch, result);
            if (!_matches.isEmpty()) {
              matches.add(_matches);
              break;
            }
            QueryTracer.leave(branch);
          }
        } else {
          if (clause.getAlternatives().size() == 1) {
            // optional clause
            List branch = (List) clause.getAlternatives().get(0);
            QueryTracer.enter(branch);
            matches = satisfy(branch, result);
            QueryTracer.leave(branch);
  
            matches.addNonRedundant(result);
            
          } else {
            // ordinary OR
            Iterator it = clause.getAlternatives().iterator();
            while (it.hasNext()) {
              List branch = (List) it.next();
              QueryTracer.enter(branch);
              matches.add(satisfy(branch, result));
              QueryTracer.leave(branch);
            }
          }
        }

        result = matches;
        QueryTracer.leave(result);
        
      } else if (theClause instanceof NotClause) {
        NotClause clause = (NotClause) theClause;
        QueryTracer.enter(NOT_PREDICATE, clause, result);
        QueryMatches notmatches = satisfy(clause.getClauses(), result);
        QueryMatches matches = new QueryMatches(result);
        matches.add(result); // making a copy
        matches.remove(notmatches);
        result = matches;
        QueryTracer.leave(result);
        
      } else {
        throw new OntopiaRuntimeException("Unknown clause type:" + theClause);
      }

      // if there are no matches, there's no need to continue, as later
      // clauses can't generate any from nothing
      if (result.last == -1) {
        return result;
      }
    }
    
    return result;
  }

  private static Object[] makeArgumentArray(AbstractClause clause, QueryContext context) {
    Object[] args = clause.getArguments().toArray();
    for (int ix = 0; ix < args.length; ix++) {
      if (args[ix] instanceof Parameter) {
        args[ix] = context.getParameterValue(((Parameter) args[ix]).getName());
      }
    }
    return args;
  }

  // --- Internal helper class

  // only used to produce debugging traces
  
  static class NotPredicate implements BasicPredicateIF {
    private static final String MSG_INTERNAL_ERROR = "INTERNAL ERROR";
    
    @Override
    public String getName() {
      throw new OntopiaRuntimeException(MSG_INTERNAL_ERROR);
    }

    @Override
    public String getSignature() {
      throw new OntopiaRuntimeException(MSG_INTERNAL_ERROR);
    }

    @Override
    public int getCost(boolean[] boundparam) {
      throw new OntopiaRuntimeException(MSG_INTERNAL_ERROR);
    }
  
    @Override
    public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
      throws InvalidQueryException {
      throw new OntopiaRuntimeException(MSG_INTERNAL_ERROR);
    }

  }
  
}
