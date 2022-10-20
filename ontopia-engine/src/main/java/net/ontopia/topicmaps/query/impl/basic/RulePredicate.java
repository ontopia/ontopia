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

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ontopia.utils.CompactHashSet;
import net.ontopia.topicmaps.impl.utils.ArgumentValidator;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.QueryAnalyzer;
import net.ontopia.topicmaps.query.impl.utils.QueryOptimizer;
import net.ontopia.topicmaps.query.parser.AbstractClause;
import net.ontopia.topicmaps.query.parser.Pair;
import net.ontopia.topicmaps.query.parser.ParsedRule;
import net.ontopia.topicmaps.query.parser.PredicateClause;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.query.impl.utils.CostEstimator;
import net.ontopia.topicmaps.query.impl.utils.SimpleCostEstimator;

/**
 * INTERNAL: Implements rule predicates.
 */
public class RulePredicate extends AbstractQueryProcessor
                           implements BasicPredicateIF {
  protected ParsedRule rule;
  protected String signature;
  
  public RulePredicate(ParsedRule rule) {
    this.rule = rule;
  }
  
  // --- PredicateIF implementation

  @Override
  public String getName() {
    return rule.getName();
  }

  @Override
  public String getSignature() throws InvalidQueryException {
    if (signature != null) {
      return signature;
    }

    // protect against infinite recursion
    List params = rule.getParameters();
    StringBuilder sign = new StringBuilder();
    for (int ix = 0; ix < params.size(); ix++) {
      if (ix > 0) {
        sign.append(' ');
      }
      sign.append('.');
    }
    signature = sign.toString();
    // we do not return here, but try to produce a stricter value
    
    // do type analysis on rule
    boolean strict = rule.getOptions().getBooleanValue("compiler.typecheck");
    Map vartypes = QueryAnalyzer.analyzeTypes(rule.getClauses(), strict)
                     .getVariableTypes();
    
    // produce corresponding signature
    sign = new StringBuilder();
    for (int ix = 0; ix < params.size(); ix++) {
      Variable var = (Variable) params.get(ix);
      if (ix > 0) {
        sign.append(' ');
      }
      sign.append(ArgumentValidator.makeSignature((Object[]) vartypes.get(var.getName())));
    }
    signature = sign.toString();
    return sign.toString();
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    int open = 0;
    for (int ix = 0; ix < boundparams.length; ix++) {
      if (!boundparams[ix]) {
        open++;
      }
    }

    if (open == 0) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else {
      // we want to punish rules which have many open variables;
      // at the same time we want to run rules early. this represents
      // a compromise.
      return PredicateDrivenCostEstimator.BIG_RESULT + open - 1;
    }
  }
  
  // --- BasicPredicateIF implementation
  
  @Override
  public QueryMatches satisfy(QueryMatches extmatches, Object[] extarguments)
    throws InvalidQueryException {

    QueryContext extcontext = extmatches.getQueryContext();
    
    // build a new matches object for the internal clauses
    Collection items = findClauseItems(rule.getClauses(), extcontext.getParameters());

    QueryContext intcontext = new QueryContext(extcontext.getTopicMap(), null,
                                               extcontext.getParameters(),
                                               extcontext.getTologOptions());
    QueryMatches intmatches = new QueryMatches(items, intcontext);

    Object[] params = rule.getParameters().toArray();
    
    // find connections between internal and external matches
    int[][] translationSpec = extmatches.getTranslationSpec(extarguments,
                                                            intmatches,
                                                            params);
    int[] extspec = translationSpec[0];
    int[] intspec = translationSpec[1];
//     QueryTracer.trace("=====>" + getName());
//     QueryTracer.trace("extspec", extspec);
//     QueryTracer.trace("intspec", intspec);
//     QueryTracer.trace("extcols", extmatches.columnDefinitions);
//     QueryTracer.trace("intcols", intmatches.columnDefinitions);
    
    // translate external matches into internal matches
    extmatches.translate(extspec, intmatches, intspec);

    // insert the constants in their columns
    intmatches.insertConstants();
    
    // run satisfy in the usual way
    Set bound = getBoundVariables(params, extarguments, extmatches);
    Set litvars = getLiteralVariables(params, extarguments);
    List theclauses = rule.getClauses();
    if (extcontext.getTologOptions().getBooleanValue("optimizer.reorder")) {
      CostEstimator estimator;
      if (extcontext.getTologOptions().getBooleanValue("optimizer.reorder.predicate-based")) {
        estimator = new PredicateDrivenCostEstimator();
      } else {
        estimator = new SimpleCostEstimator();
      }
      theclauses = QueryOptimizer.reorder(theclauses, bound, litvars,
                                          getName(), estimator);
    }
    
    intmatches = satisfy(theclauses, intmatches);
    
    // merge external matches with internal matches
    return extmatches.merge(extspec, intmatches, intspec,
                            getEqualPairs(extarguments));
  }

  /**
   * INTERNAL: Finds the variables that are bound inside the rule in
   * this particular invocation of it.
   * @param params The parameters to the rule given in its declaration
   *               (an array of Variable objects)
   * @param extarguments The parameters passed to this invocation
   * @param extmatches The current temporary query result.
   */
  private static Set getBoundVariables(Object[] params, Object[] extarguments,
                                       QueryMatches extmatches) {
    Set bound = new CompactHashSet();
    for (int ix = 0; ix < params.length; ix++) {
      int col = extmatches.getIndex(extarguments[ix]);
      if (extmatches.bound(col)) {
        bound.add(params[ix]);
      }
    }

    return bound;
  }

  /**
   * INTERNAL: Finds the variables inside the rule that were bound to
   * literals outside the rule.
   * @param params The parameters to the rule given in its declaration
   *               (an array of Variable objects)
   * @param extarguments The parameters passed to this invocation
   */
  private static Set getLiteralVariables(Object[] params, Object[] extarguments) {
    Set litvars = new CompactHashSet();
    for (int ix = 0; ix < params.length; ix++) {
      if (!(extarguments[ix] instanceof Variable)) {
        litvars.add(params[ix]);
      }
    }

    return litvars;
  }
  
  // --- Various public methods

  public List getClauses() {
    return rule.getClauses();
  }

  public List getParameters() {
    return rule.getParameters();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof RulePredicate) {
      RulePredicate other = (RulePredicate)obj;
      return rule.equals(other.rule);
    }
    return false;
  }

  /**
   * INTERNAL: Checks to see if this rule is simply an alias. If it is
   * the optimizer can do an inline replacement of it to optimize other
   * rules as well as queries that use it.
   */
  public boolean replaceable() {
    if (rule.getClauses().size() != 1) {
      return false;
    }

    AbstractClause clause = (AbstractClause) rule.getClauses().get(0);
    if (!(clause instanceof PredicateClause)) {
      return false;
    }
    
    Collection variables = findClauseVariables(getClauses());
    List parameters = getParameters();
    if (variables.size() != parameters.size()) {
      return false;
    }
    
    for (int ix = 0; ix < parameters.size(); ix++) {
      if (!variables.contains(parameters.get(ix))) {
        return false;
      }
    }

    return true;
  }

  /**
   * INTERNAL: Creates a new PredicateClause representing the content
   * of this rule inlined in an environment where the arguments in the
   * args parameter have been passed to the predicate.
   */
  public PredicateClause translate(List arguments) {
    PredicateClause srcclause = (PredicateClause) rule.getClauses().get(0);
    PredicateClause clause = new PredicateClause(srcclause.getPredicate());

    Map varmap = makeVariableMap(arguments);
    List srcargs = srcclause.getArguments();
    for (int ix = 0; ix < srcargs.size(); ix++) {
      Object arg = srcargs.get(ix);
      Object newarg;

      if (arg instanceof Pair) {
        Pair pair = (Pair) arg;
        if (pair.getFirst() instanceof Variable) {
          newarg = new Pair(varmap.get(pair.getFirst()), pair.getSecond());
        } else {
          newarg = new Pair(pair.getFirst(), pair.getSecond());
        }
      } else if (arg instanceof Variable) {
        newarg = varmap.get(arg);
      } else {
        newarg = arg;
      }
      
      clause.addArgument(newarg);
    }

    return clause;
  }

  // maps params -> args
  private Map makeVariableMap(List arguments) {
    List params = getParameters();
    Map varmap = new HashMap();
    for (int ix = 0; ix < arguments.size(); ix++) {
      varmap.put(params.get(ix), arguments.get(ix));
    }
    return varmap;
  }

  /**
   * INTERNAL: Finds pairs of equal variables in the arguments
   * received by the rule. Returns an array where items 2n and 2n+1
   * (for all n) are references to internal columns that externally
   * are bound to the same variable. That is, let's say a rule is
   * invoked as rule($A, $B, $A, $C, $B), then the array returned will
   * be (0, 2, 1, 4), which means that columns 0 and 2 must be equal,
   * and columns 1 and 4 must be.
   * @return array with indexes referring to argument number
   */
  private int[] getEqualPairs(Object[] extarguments) {
    List<Integer> l = new ArrayList<Integer>();
    for (int ix = 0; ix+1 < extarguments.length; ix++) {
      for (int i = ix+1; i < extarguments.length; i++) {
        if (extarguments[ix] instanceof Variable &&
            extarguments[i] instanceof Variable &&
            extarguments[ix].equals(extarguments[i])) {
          l.add(ix);
          l.add(i);
        }
      }
    }

    int[] pairs = new int[l.size()];
    for (int ix = 0; ix < l.size(); ix++) {
      pairs[ix] = l.get(ix).intValue();
    }
    
    return pairs;
  }
}
