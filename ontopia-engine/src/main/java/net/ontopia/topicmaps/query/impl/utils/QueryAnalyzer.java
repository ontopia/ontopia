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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.NotClause;
import net.ontopia.topicmaps.query.parser.OrClause;
import net.ontopia.topicmaps.query.parser.Pair;
import net.ontopia.topicmaps.query.parser.PredicateClause;
import net.ontopia.topicmaps.query.parser.PredicateIF;
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * INTERNAL: Analyzes the types of variables in the query. Finds the
 * type(s) of each variable and reports typing errors.
 */
public class QueryAnalyzer {

  private static final Class[] TYPES_TOPIC = { TopicIF.class };

  public static BindingContext analyzeTypes(TologQuery query) 
    throws InvalidQueryException {
    boolean strict = query.getOptions().getBooleanValue("compiler.typecheck");
    BindingContext bc = new BindingContext(strict);
    analyzeTypes(query.getClauses(), bc);
    return bc;
  }

  public static BindingContext analyzeTypes(List clauses, boolean strict)
    throws InvalidQueryException {
    BindingContext bc = new BindingContext(strict);
    analyzeTypes(clauses, bc);
    return bc;
  }
  
  public static void analyzeTypes(List clauses, BindingContext bc) 
    throws InvalidQueryException {

    // --- HOW IT WORKS
    
    // predicate clauses define the types of their arguments directly

    // the allowed types for the same variable from different
    // predicates in the same horn clause (AND list) are combined with
    // INTERSECTION

    // the allowed types for the same variable from different branches
    // of an OR clause are combined with UNION

    // the allowed types for the same variable outside and inside an
    // OPTIONAL clause are as given from the outside, except if
    // nothing is given outside, in which case they are as given
    // inside
    
    // the allowed types for the same variable outside and inside a
    // NOT clause are combined with INTERSECTION
    
    // Process clauses contexts; OR, NOT (if any)
    Iterator iter = clauses.iterator();
    while (iter.hasNext()) {
      Object clause = iter.next();

      if (clause instanceof PredicateClause) {
        // Analyze the predicate and its arguments
        PredicateClause pc = (PredicateClause) clause;       
        analyzeArguments(pc, bc);

      } else if (clause instanceof OrClause) {
        List alternatives = ((OrClause) clause).getAlternatives();
        BindingContext bc1 = new BindingContext(bc.getCheckingTypes());

        if (alternatives.size() == 1) {
          // optional clause
          List aclauses = (List) alternatives.get(0);
          analyzeTypes(aclauses, bc1);

          bc.mergeAssymetric(bc1);
        } else {
          // ordinary OR clause
          Iterator alts = alternatives.iterator();
          while(alts.hasNext()) {
            // Each alternative introduces a new binding context
            BindingContext bc2 = new BindingContext(bc.getCheckingTypes());
            List aclauses = (List)alts.next();
            analyzeTypes(aclauses, bc2);

            // merge bc2 with parent bc1
            bc1.mergeUnion(bc2);
          }

          // merge bc1 with parent bc
          bc.mergeIntersect(bc1);
        }
        
          
      } else if (clause instanceof NotClause) {
        NotClause nclause = (NotClause) clause;
        // Not clause introduces a new binding context
        BindingContext bc1 = new BindingContext(bc.getCheckingTypes());
        analyzeTypes(nclause.getClauses(), bc1);
        bc.mergeAssymetric(bc1);
      }
    }
  }

  private static void analyzeArguments(PredicateClause clause, BindingContext bc)
    throws InvalidQueryException {
    
    PredicateIF predicate = clause.getPredicate();
    PredicateSignature sign = PredicateSignature.getSignature(predicate);
    Object[] args = clause.getArguments().toArray();
    sign.validateArguments(args, predicate.getName(), bc.getCheckingTypes());
    
    for (int ix = 0; ix < args.length; ix++) {
      Class[] types = sign.getTypes(ix);
      if (types.length == 1 && types[0].equals(Pair.class)) {
        types = TYPES_TOPIC;
      }

      bc.addArgumentTypes(args[ix], types, predicate);
    }
  }

  /**
   * Verifies that all used parameters are specified and that they are
   * of the correct types. We don't care if too many arguments are
   * specified so long as all the ones used in the query are right.
   */
  public static void verifyParameters(TologQuery query, Map arguments)
    throws InvalidQueryException {

    boolean typecheck =
      query.getOptions().getBooleanValue("compiler.typecheck");
    Map ptypes = query.getParameterTypes();
    Iterator it = ptypes.keySet().iterator();
    while (it.hasNext()) {
      String parname = (String) it.next();
      Object value = (arguments == null ? null : arguments.get(parname));

      if (value == null) {
        throw new InvalidQueryException("Parameter " + parname + " not specified");
      }

      boolean ok = false;
      Object[] types = (Object[]) ptypes.get(parname);
      for (int ix = 0; !ok && ix < types.length; ix++) {
        Class type = (Class) types[ix];
        ok = type.isAssignableFrom(value.getClass());
      }
      if (!ok & typecheck) {
        throw new InvalidQueryException(
          "Parameter " + parname + " must be " +
          PredicateSignature.getClassList(types) + ", but was " + value);
      }
                                        
    }
  }

}
