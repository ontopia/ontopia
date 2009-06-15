
// $Id: PredicateDrivenCostEstimator.java,v 1.4 2007/10/30 12:55:37 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Set;
import java.util.List;
import net.ontopia.topicmaps.query.parser.Pair;
import net.ontopia.topicmaps.query.parser.OrClause;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.topicmaps.query.parser.NotClause;
import net.ontopia.topicmaps.query.parser.PredicateIF;
import net.ontopia.topicmaps.query.parser.AbstractClause;
import net.ontopia.topicmaps.query.parser.PredicateClause;

public class PredicateDrivenCostEstimator extends CostEstimator {
  // cost constants
  public static int INFINITE_RESULT = 10000000;
  public static int WHOLE_TM_RESULT = 1000;
  public static int BIG_RESULT      = 100;
  public static int MEDIUM_RESULT   = 10;
  public static int SMALL_RESULT    = 2;
  public static int SINGLE_RESULT   = 1;
  public static int FILTER_RESULT   = 0;
  public static int FAIL_RESULT     = -1;

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
  public int computeCost(Set context, AbstractClause clause,
                         Set literalvars, String rulename) {
    int cost = 0;

    if (clause instanceof PredicateClause) {
      // ask the predicate, then set the cost as requested
      PredicateClause pclause = (PredicateClause) clause;
      PredicateIF predicate = pclause.getPredicate();
      boolean[] boundparams = getBoundParameters(pclause, context);
      cost = predicate.getCost(boundparams);

      // postpone recursive evaluation
      if (rulename != null && predicate.getName().equals(rulename))
        cost += BIG_RESULT;
      
    } else if (clause instanceof NotClause)
      // set the cost to something bigger than the others
      cost = INFINITE_RESULT + 2;
    else if (clause instanceof OrClause &&
             ((OrClause) clause).getAlternatives().size() == 1)
      // set the cost to something big
      cost = INFINITE_RESULT + 1;
    else if (clause instanceof OrClause) 
      cost = computeCost((OrClause) clause, context, literalvars, rulename);
    
    return cost;
  }

  private int computeCost(OrClause or, Set context, Set literalvars,
                          String rulename) {
    int worstcost = -1;
    
    List alternatives = or.getAlternatives();
    for (int ix = 0; ix < alternatives.size(); ix++) {
      List alternative = (List) alternatives.get(ix);
      AbstractClause clause = (AbstractClause) alternative.get(0);
      int cost = computeCost(context, clause, literalvars, rulename);
      worstcost = Math.max(cost, worstcost);
    }

    return worstcost;
  }

  private boolean[] getBoundParameters(PredicateClause clause,
                                       Set context) {
    List args = clause.getArguments();
    boolean[] boundparams = new boolean[args.size()];
    for (int ix = 0; ix < boundparams.length; ix++) {
      Object arg = args.get(ix);

      if (arg instanceof Pair) {
        Pair p = (Pair) arg;
        arg = p.getFirst();
      }
      
      // in other words: bound unless this is a variable not in the context
      // other alternatives: parameter (must be bound) or a literal
      boundparams[ix] =
        (!(arg instanceof Variable)) ||
        (arg instanceof Variable && context.contains(arg));
    }
    return boundparams;
  }

  public static int getComparisonPredicateCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1])
      return FILTER_RESULT;
    else
      return INFINITE_RESULT;
  }
}