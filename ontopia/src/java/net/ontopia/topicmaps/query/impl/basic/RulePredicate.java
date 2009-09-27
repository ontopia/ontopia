
// $Id: RulePredicate.java,v 1.35 2008/07/23 11:24:23 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.basic;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  public String getName() {
    return rule.getName();
  }

  public String getSignature() throws InvalidQueryException {
    if (signature != null)
      return signature;

    // protect against infinite recursion
    List params = rule.getParameters();
    StringBuffer sign = new StringBuffer();
    for (int ix = 0; ix < params.size(); ix++) {
      if (ix > 0) sign.append(' ');
      sign.append('.');
    }
    signature = sign.toString();
    // we do not return here, but try to produce a stricter value
    
    // do type analysis on rule
    boolean strict = rule.getOptions().getBooleanValue("compiler.typecheck");
    Map vartypes = QueryAnalyzer.analyzeTypes(rule.getClauses(), strict)
                     .getVariableTypes();
    
    // produce corresponding signature
    sign = new StringBuffer();
    for (int ix = 0; ix < params.size(); ix++) {
      Variable var = (Variable) params.get(ix);
      if (ix > 0)
        sign.append(' ');
      sign.append(ArgumentValidator.makeSignature((Object[]) vartypes.get(var.getName())));
    }
    signature = sign.toString();
    return sign.toString();
  }
  
  public int getCost(boolean[] boundparams) {
    int open = 0;
    for (int ix = 0; ix < boundparams.length; ix++)
      if (!boundparams[ix])
        open++;

    if (open == 0)
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    else
      return PredicateDrivenCostEstimator.BIG_RESULT + open;
  }
  
  // --- BasicPredicateIF implementation
  
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
    int[][] translationSpec = extmatches.getTranslationSpec(extarguments, intmatches,
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
    // FIXME: this throws the remove-duplicates() predicates out of whack
    Set bound = getBoundVariables(params, extarguments, extmatches);
    Set litvars = getLiteralVariables(params, extarguments);
    List theclauses = rule.getClauses();
    if (extcontext.getTologOptions().getBooleanValue("optimizer.reorder")) {
      CostEstimator estimator;
      if (extcontext.getTologOptions().getBooleanValue("optimizer.reorder.predicate-based"))
        estimator = new PredicateDrivenCostEstimator();
      else
        estimator = new SimpleCostEstimator();
      theclauses = QueryOptimizer.reorder(theclauses, bound, litvars,
                                          getName(), estimator);
    }
    
    intmatches = satisfy(theclauses, intmatches);
    
    // merge external matches with internal matches
    return extmatches.merge(extspec, intmatches, intspec);
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
    Set bound = new HashSet();
    for (int ix = 0; ix < params.length; ix++) {
      int col = extmatches.getIndex(extarguments[ix]);
      if (extmatches.bound(col))
        bound.add(params[ix]);
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
    Set litvars = new HashSet();
    for (int ix = 0; ix < params.length; ix++)
      if (!(extarguments[ix] instanceof Variable))
        litvars.add(params[ix]);

    return litvars;
  }
  
  // --- Various public methods

  public List getClauses() {
    return rule.getClauses();
  }

  public List getParameters() {
    return rule.getParameters();
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
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
    if (rule.getClauses().size() != 1)
      return false;

    AbstractClause clause = (AbstractClause) rule.getClauses().get(0);
    if (!(clause instanceof PredicateClause))
      return false;
    
    Collection variables = findClauseVariables(getClauses());
    List parameters = getParameters();
    if (variables.size() != parameters.size())
      return false;
    
    for (int ix = 0; ix < parameters.size(); ix++)
      if (!variables.contains(parameters.get(ix)))
        return false;

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
        if (pair.getFirst() instanceof Variable)
          newarg = new Pair(varmap.get(pair.getFirst()), pair.getSecond());
        else
          newarg = new Pair(pair.getFirst(), pair.getSecond());
      } else if (arg instanceof Variable)
        newarg = varmap.get(arg);
      else
        newarg = arg;
      
      clause.addArgument(newarg);
    }

    return clause;
  }

  // maps params -> args
  private Map makeVariableMap(List arguments) {
    List params = getParameters();
    Map varmap = new HashMap();
    for (int ix = 0; ix < arguments.size(); ix++) 
      varmap.put(params.get(ix), arguments.get(ix));
    return varmap;
  }
  
}
