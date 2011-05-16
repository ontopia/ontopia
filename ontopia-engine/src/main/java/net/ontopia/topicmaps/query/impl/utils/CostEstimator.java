
// $Id: CostEstimator.java,v 1.1 2007/09/19 13:16:36 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Set;
import net.ontopia.topicmaps.query.parser.AbstractClause;

public abstract class CostEstimator {
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
  public abstract int computeCost(Set context, AbstractClause clause,
                                  Set literalvars, String rulename);
}
