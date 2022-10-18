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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ontopia.utils.CompactHashSet;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.AbstractQueryProcessor;
import net.ontopia.topicmaps.query.impl.basic.DynamicFailurePredicate;
import net.ontopia.topicmaps.query.impl.basic.DynamicOccurrencePredicate;
import net.ontopia.topicmaps.query.impl.basic.RemoveDuplicatesPredicate;
import net.ontopia.topicmaps.query.impl.basic.RolePlayerPredicate;
import net.ontopia.topicmaps.query.impl.basic.RulePredicate;
import net.ontopia.topicmaps.query.impl.basic.StringModule;
import net.ontopia.topicmaps.query.impl.basic.TypePredicate;
import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;
import net.ontopia.topicmaps.query.impl.basic.OccurrencePredicate;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.impl.basic.GreaterThanPredicate;
import net.ontopia.topicmaps.query.impl.basic.GreaterThanEqualsPredicate;
import net.ontopia.topicmaps.query.impl.basic.LessThanPredicate;
import net.ontopia.topicmaps.query.impl.basic.LessThanEqualsPredicate;
import net.ontopia.topicmaps.query.parser.AbstractClause;
import net.ontopia.topicmaps.query.parser.NotClause;
import net.ontopia.topicmaps.query.parser.OrClause;
import net.ontopia.topicmaps.query.parser.Pair;
import net.ontopia.topicmaps.query.parser.Parameter;
import net.ontopia.topicmaps.query.parser.ParsedRule;
import net.ontopia.topicmaps.query.parser.PredicateClause;
import net.ontopia.topicmaps.query.parser.PredicateIF;
import net.ontopia.topicmaps.query.parser.TologOptions;
import net.ontopia.topicmaps.query.parser.TologQuery;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.utils.OntopiaRuntimeException;

// TODO
//  - change architecture of optimizer so that individual optimizers
//    can be simpler
//  - change so that optimizers can be applied in a specific order

/**
 * INTERNAL: An optimizer class that knows how to rewrite queries to
 * equivalent, but more efficient queries. Used by the different query
 * processor implementations to improve performance. Note that the
 * only optimizations this class should perform are those which are
 * independent of the tolog implementation used and which only rely
 * on the semantics of tolog.</p>
 *
 * <p>The only optimizations performed at the moment are:</p>
 *
 * <ul>
 *   <li>Reordering of query clauses for better performance by limiting
 *   the number of intermediate results.</li>
 *   <li>Inlining rules which are simple aliases for a single predicate.
 * </ul>
 *
 */
public class QueryOptimizer {
  private List optimizers;
  private static final Class[] TYPES_TOPIC = { TopicIF.class };

  /**
   * INTERNAL: Get hold of an query optimizer instance.
   * @param query The parsed query.
   */
  public static QueryOptimizer getOptimizer(TologQuery query) {
    // WARNING: method used by basic+rdbms tolog
    TologOptions options = query.getOptions();
    QueryOptimizer optimizer = new QueryOptimizer();

    if (options != null) {
      if (options.getBooleanValue("optimizer.inliner"))
        optimizer.addOptimizer(new QueryOptimizer.RuleInliner());
      if (options.getBooleanValue("optimizer.reorder")) {
        // NOTE: new optimizer is now on by default
        boolean newapproach =
          options.getBooleanValue("optimizer.reorder.predicate-based");
        optimizer.addOptimizer(new QueryOptimizer.Reorderer(newapproach));
      }
      if (options.getBooleanValue("optimizer.typeconflict"))
        optimizer.addOptimizer(new QueryOptimizer.TypeConflictResolver());
      if (options.getBooleanValue("optimizer.hierarchy-walker"))
        optimizer.addOptimizer(new QueryOptimizer.HierarchyWalker());
      if (options.getBooleanValue("optimizer.prefix-search"))
        optimizer.addOptimizer(new QueryOptimizer.StringPrefixOptimizer());
      if (options.getBooleanValue("optimizer.role-player-type"))
        optimizer.addOptimizer(new QueryOptimizer.AddTypeToRolePlayer());
      if (options.getBooleanValue("optimizer.next-previous"))
        optimizer.addOptimizer(new QueryOptimizer.NextPreviousOptimizer());
    }
    return optimizer;
  }

  // ===== THE OPTIMIZER =====================================================

  public QueryOptimizer() {
    optimizers = new ArrayList();
  }

  public void addOptimizer(QueryOptimizerIF optimizer) {
    optimizers.add(optimizer);
  }

  public TologQuery optimize(TologQuery query)
    throws InvalidQueryException {
    
    QueryContext context = new QueryContext(query);
    for (int ix = 0; ix < optimizers.size(); ix++) {
      QueryOptimizerIF optimizer = (QueryOptimizerIF) optimizers.get(ix);
      optimizer.optimize(query, context);
    }
    
    query.setClauseList(optimize(query.getClauses(), context));
    return query;
  }

  public ParsedRule optimize(ParsedRule rule)
    throws InvalidQueryException {

    QueryContext context = new QueryContext(null, rule);
    rule.setClauseList(optimize(rule.getClauses(), context));
    return rule;
  }
  
  public List optimize(List clauses, QueryContext context)
    throws InvalidQueryException {

    context.enterClauseList();
    
    List newclauses = new ArrayList();
    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);

      if (clause instanceof PredicateClause) {
        PredicateClause pclause = (PredicateClause) clause;
        for (int i = 0; i < optimizers.size(); i++)
          pclause = ((QueryOptimizerIF) optimizers.get(i)).optimize(pclause, context);
        clause = pclause;
        
      } else if (clause instanceof OrClause) {
        List alts = ((OrClause) clause).getAlternatives();
        for (int i = 0; i < alts.size(); i++)
          alts.set(i, optimize((List) alts.get(i), context));
            
      } else if (clause instanceof NotClause) {
        List notted = ((NotClause) clause).getClauses();
        clause = new NotClause(optimize(notted, context));

      }

      newclauses.add(clause);
    }

    clauses = newclauses;
    for (int ix = 0; ix < optimizers.size(); ix++)
      clauses = ((QueryOptimizerIF) optimizers.get(ix)).optimize(clauses, context);

    context.leaveClauseList();
    return clauses;
  }

  // ===== ABSTRACT OPTIMIZER ===================================================
  
  public static abstract class AbstractQueryOptimizer implements QueryOptimizerIF {

    @Override
    public void optimize(TologQuery query, QueryContext context)
      throws InvalidQueryException {
      // do nothing
    }
    
    @Override
    public PredicateClause optimize(PredicateClause clause, QueryContext context)
      throws InvalidQueryException {
      return clause;
    }
    
    @Override
    public List optimize(List clauses, QueryContext context)
      throws InvalidQueryException {
      return clauses;
    }
    
  }
  
  // ===== INLINING =============================================================

  /**
   * INTERNAL: Optimizes the query by inlining all rules which are simple
   * aliases for a single predicate.
   */
  public static class RuleInliner extends AbstractQueryOptimizer {
  
    @Override
    public PredicateClause optimize(PredicateClause clause, QueryContext context) {
      return clause.getReplacement();
    }
  }

  // ===== REORDERING ========================================================
  
  //   Rules for optimization of clause order:
  //   
  //     1. start with empty context
  //     2. find the clause with the lowest cost
  //     3. add variables bound by this clause to context
  //     4. if more clauses goto 2
  //   
  //   To compute cost within context: use an estimator. There are
  //   two, which can be swapped, and more can be added.

  /**
   * INTERNAL: Optimizes the query by reordering the clauses into the
   * optimal order for evaluation. See
   * http://www.ontopia.net/topicmaps/materials/tolog.html, under
   * 'Optimizing queries'.
   */
  public static class Reorderer extends AbstractQueryOptimizer {
    private boolean predicate_based;

    public Reorderer(boolean predicate_based) {
      this.predicate_based = predicate_based;
    }
    
    @Override
    public List optimize(List qclauses, QueryContext qcontext) {
      if (qcontext.getNestingLevel() > 1)
        return qclauses;

      CostEstimator estimator;
      if (predicate_based)
        estimator = new PredicateDrivenCostEstimator();
      else
        estimator = new SimpleCostEstimator();
      return reorder(qclauses, Collections.EMPTY_SET, Collections.EMPTY_SET,
                     null, estimator);
    }
  }
  
  /**
   * INTERNAL: Optimizes the order of the query clauses in a context
   * where the given variables are bound. Done as a static method so
   * we can use it from within RulePredicate.
   *
   * @param qclauses The list of clauses to be reordered.
   * @param boundvars Contains the variables bound when we get here
   * @param literalvars Contains the variables representing literals.
   *                    Only an issue in rules.
   * @param rulename The name of the current rule (so we can delay
   *                 recursive evaluation).
   */
  public static List reorder(List qclauses, Set boundvars, Set literalvars,
                             String rulename, CostEstimator estimator) {
    List clauses = new ArrayList(qclauses);
    List newOrder = new ArrayList();
    Set context = new CompactHashSet(boundvars);
    while (clauses.size() > 0) {
      int lowest = Integer.MAX_VALUE;
      int best = 0;

      //System.out.println("-------------------------------------------------");
      
      // find current best clause
      for (int ix = 0; ix < clauses.size(); ix++) {
        int cost = estimator.computeCost(context,
                                         (AbstractClause) clauses.get(ix),
                                         literalvars, rulename);
        //System.out.println("cost (" + cost + "): " + clauses.get(ix));
        
        if (cost < lowest) {
          lowest = cost;
          best = ix;
        }
      }

      //System.out.println("best (" + lowest +"): " + clauses.get(best));
      
      // update based on choice
      AbstractClause clause = (AbstractClause) clauses.get(best);
      if (clause instanceof OrClause) 
        reorder((OrClause) clause, boundvars, literalvars, rulename, estimator);
      else if (clause instanceof NotClause)
        reorder((NotClause) clause, boundvars, literalvars, rulename, estimator);
      context.addAll(clause.getAllVariables());
      
      newOrder.add(clauses.get(best));
      clauses.remove(best);
    }

    qclauses.clear();
    for (int ix = 0; ix < newOrder.size(); ix++)
      qclauses.add(newOrder.get(ix));

    return newOrder;
  }

  /**
   * INTERNAL: Reorders the clauses inside the alternatives in the OR
   * branch. Useful for better performance in some cases, and
   * certainly for fixing bug #1229.
   */
  private static void reorder(OrClause clause, Set boundvars, Set literalvars,
                              String rulename, CostEstimator estimator) {
    int ix = 0;
    List alts = clause.getAlternatives();
    Iterator it = alts.iterator();
    while (it.hasNext()) {
      List clauses = (List) it.next();
      List newclauses = reorder(clauses, new CompactHashSet(boundvars),
                                new CompactHashSet(literalvars), rulename,
                                estimator);
      alts.set(ix++, newclauses);
    }
  }

  /**
   * INTERNAL: Reorders the clauses inside the alternatives in the NOT
   * branch. 
   */
  private static void reorder(NotClause clause, Set boundvars, Set literalvars,
                              String rulename, CostEstimator estimator) {
    List newclauses = reorder(clause.getClauses(),
                              new CompactHashSet(boundvars),
                              new CompactHashSet(literalvars),
                              rulename,
                              estimator);
    clause.setClauseList(newclauses);
  }

  // ===== RECURSIVE DUPLICATES ================================================

  /**
   * This optimizer adds RemoveDuplicatesPredicate on both sides of
   * recursive calls within predicate rules. Recursive rules tend to
   * generate lots of redundant temporary results, which again
   * generate more redundant junk. Removing duplicates cuts down the
   * junk dramatically. This optimizer fixes bug #791.
   */
  public static class RecursivePruner extends AbstractQueryOptimizer {

    @Override
    public List optimize(List clauses, QueryContext context) {
      if (context.getRuleName() == null)
        return clauses; // queries are never recursive, only rules

      List newclauses = new ArrayList();
      for (int ix = 0; ix < clauses.size(); ix++) {
        AbstractClause clause = (AbstractClause) clauses.get(ix);

        if (clause instanceof PredicateClause &&
            ((PredicateClause) clause).getPredicate() instanceof RulePredicate) {
          // adding on both sides because that proved to be fastest
          newclauses.add(new PredicateClause(new RemoveDuplicatesPredicate(true)));
          newclauses.add(clause);
          newclauses.add(new PredicateClause(new RemoveDuplicatesPredicate(false)));
          
        } else
          newclauses.add(clause);
      }

      return newclauses;
    }
    
  }

  // ===== TYPE CONFLICT RESOLVER ==============================================

  /**
   * Finds cases of conflicting variables and resolves them by
   * replacing predicates which can never succeed with
   * DynamicFailurePredicate.
   */
  public static class TypeConflictResolver extends AbstractQueryOptimizer {

    // the general rule is that vartypemap will contain the list of
    // all possible types for the variables in it. this means that if
    // it is empty there is a conflict and we can short out the
    // predicate. 
    
    @Override
    public List optimize(List clauses, QueryContext context)
      throws InvalidQueryException {
      
      Map vartypemap = context.getVariableTypes();
      Map ptypemap = context.getParameterTypes();
      
      for (int ix = 0; ix < clauses.size(); ix++) {
        AbstractClause clause = (AbstractClause) clauses.get(ix);

        if (clause instanceof PredicateClause) {
          PredicateClause pclause = (PredicateClause) clause;

          boolean ok = true;
          PredicateIF predicate = pclause.getPredicate();
          PredicateSignature signature = null;

          if (predicate instanceof PumpPredicate)
            continue;

          try {
            signature = PredicateSignature.getSignature(predicate);
          } catch (InvalidQueryException e) {
            // should not be possible to have this sort of error here. it
            // should have been caught already.
            throw new OntopiaRuntimeException("INTERNAL ERROR", e);
          }
          
          List args = pclause.getArguments();
          for (int i = 0; i < args.size() && ok; i++) {
            Object arg = args.get(i);
            if (arg instanceof Variable) {
              String varname = ((Variable) arg).getName();
              Object[] types = (Object[]) vartypemap.get(varname);
              ok = !emptyIntersection(types, signature.getTypes(i), context, arg,
                                      predicate);

            } else if (arg instanceof Parameter) {
              String pname = ((Parameter) arg).getName();
              Object[] types = (Object[]) ptypemap.get(pname);
              ok = !emptyIntersection(types, signature.getTypes(i), context, arg,
                                      predicate);
            } 
            else {
              if (arg instanceof Pair) {
                Pair pair = (Pair)arg;
                if (pair.getFirst() instanceof Variable) {
                  Variable var = (Variable) pair.getFirst();
                  Object[] types = (Object[]) vartypemap.get(var.getName());
                  ok = !emptyIntersection(TYPES_TOPIC, types, context, var,
                                          predicate);
                } else if (pair.getFirst() instanceof Parameter) {
                  Parameter par = (Parameter) pair.getFirst();
                  Object[] types = (Object[]) ptypemap.get(par.getName());
                  ok = !emptyIntersection(TYPES_TOPIC, types, context, par,
                                          predicate);
                } else
                  ok = containsAssignable(pair.getFirst().getClass(), TYPES_TOPIC);
              } else
                ok = containsAssignable(arg.getClass(), signature.getTypes(i));
            }
          }

          if (!ok)
            clauses.set(ix, new PredicateClause(new DynamicFailurePredicate(),
                                                pclause.getArguments()));

        } else if (clause instanceof OrClause) {
          List alts = ((OrClause) clause).getAlternatives();
          for (int i = 0; i < alts.size(); i++)
            alts.set(i, optimize((List) alts.get(i), context));
          
        } else if (clause instanceof NotClause) {
          List notted = ((NotClause) clause).getClauses();
          clause = new NotClause(optimize(notted, context));
          
        }
      }
      
      return clauses;
    }

    private boolean emptyIntersection(Object[] types1, Object[] types2,
                                      QueryContext context, Object arg,
                                      PredicateIF predicate)
      throws InvalidQueryException {
      
      if (types1 == null || types2 == null)
        return true;

      boolean empty = true;
      for (int ix = 0; ix < types1.length; ix++)
        for (int i = 0; empty && i < types2.length; i++)
          if (types1[ix].equals(types2[i]) ||
              types1[ix].equals(Object.class) ||
              types2[i].equals(Object.class))
            empty = false;

      if (empty && context.getBooleanOption("compiler.typecheck"))
        throw new InvalidQueryException(
          "Type conflict on " + arg + ": cannot be both " +
          PredicateSignature.getClassList(types1) + " and, as required by " +
          "predicate '" + predicate.getName() + "', " +
          PredicateSignature.getClassList(types2));
      
      return empty;
    }

    private boolean containsAssignable(Class type1, Object[] types2) {
      for (int i = 0; i < types2.length; i++)
        //! if (type1.isAssignableFrom((Class)types2[i])) return true;
        if (((Class)types2[i]).isAssignableFrom(type1))
          return true;
      
      return false;
    }
  }

  // ===== HIERARCHY WALKER =====================================================

  /**
   * Replaces simple recursive rules with a more efficient custom
   * implementation that just wraps the recursive step.
   */
  public static class HierarchyWalker extends AbstractQueryOptimizer {

    @Override
    public List optimize(List clauses, QueryContext context) {

      // scan clause list looking for suitable candidates
      for (int ix = 0; ix < clauses.size(); ix++) {
        AbstractClause clause = (AbstractClause) clauses.get(ix);

        if (clause instanceof PredicateClause) {
          PredicateClause pclause = (PredicateClause) clause;
          
          if (context.getRuleName() != null &&
              pclause.getPredicate().getName().equals(context.getRuleName()))
            continue; // can't operate inside the rule we are optimizing...
          
          PredicateIF optimized = optimize(pclause);
          if (optimized != null) {
            pclause.setPredicate(optimized);
          }
          
        } else if (clause instanceof OrClause) {
          List alts = ((OrClause) clause).getAlternatives();
          for (int i = 0; i < alts.size(); i++)
            alts.set(i, optimize((List) alts.get(i), context));
          
        } else if (clause instanceof NotClause) {
          List notted = ((NotClause) clause).getClauses();
          clause = new NotClause(optimize(notted, context));
          
        }
      }

      return clauses;
    }

    private PredicateIF optimize(PredicateClause clause) {      
      // must be a rule
      if (!(clause.getPredicate() instanceof RulePredicate))
        return null;

      RulePredicate rule = (RulePredicate) clause.getPredicate();
      List clauses = rule.getClauses();

      // must have just one OR clause
      if (clauses.size() != 1 || !(clauses.get(0) instanceof OrClause))
        return null;

      // OR clause must have only two alternatives
      OrClause or = (OrClause) clauses.get(0);
      if (or.getAlternatives().size() != 2)
        return null;

      // OR clause must not be short-circuiting
      if (or.getShortCircuit())
        return null;

      List bottom = filter((List) or.getAlternatives().get(0)); // one clause
      List pump = filter((List) or.getAlternatives().get(1));   // two clauses
      if (bottom.size() != 1) { // swap if needed
        List tmp = bottom;
        bottom = pump;
        pump = tmp;
      }

      // one alternative must be 2 predicates, the other 1
      if (bottom.size() != 1 || pump.size() != 2)
        return null;

      // bottom must not be a recursive reference
      if (!(bottom.get(0) instanceof PredicateClause))
        return null;

      PredicateClause bottomclause = (PredicateClause) bottom.get(0);
      if (bottomclause.getPredicate().getName().equals(rule.getName()))
        return null;

      // pump must be all predicates
      if (!(pump.get(0) instanceof PredicateClause &&
            pump.get(1) instanceof PredicateClause))
        return null;
      
      // pump must have one recursive reference
      int ix;
      for (ix = 0; ix < 2; ix++) {
        if (pump.get(ix) instanceof PredicateClause) {
          if (((PredicateClause) pump.get(ix)).getPredicate().getName().
              equals(rule.getName()))
            break;
        }
      }

      if (ix >= 2)
        return null; // no recursive reference found

      // structure is now as follows:
      //   rule(...) :- {
      //     whatever(...), rule(...) |
      //     whatever(...)
      //   }.
      //  where pumpclause is the rule invocation on the first line,
      //  midclause is the first whatever invocation, and
      //  bottomclause is the second whatever invocation
      PredicateClause pumpclause = (PredicateClause) pump.get(ix);
      PredicateClause midclause = (PredicateClause) pump.get(1 - ix);

      // verify that bottom and pump are same predicates (bug #2029)
      // this avoids using a pump to walk hierarchically if the rule
      // actually has the following form:
      //   rule(...) :- {
      //     foo(...) |
      //     bar(...), rule(...)
      //   }.
      if (!bottomclause.getPredicate().getName()
          .equals(midclause.getPredicate().getName()))
        return null; // predicates don't match

      // verify variables
      //  bottom: (PARENT, CHILD, *)
      //  pump:   (PARENT, MID,   *)
      //  mid:    (CHILD, MID,    *)
      //
      //  PARENT and CHILD must be rule parameters; MID cannot be
      //  (note that this code may mix up PARENT and CHILD; it should
      //  still work)

      Collection ruleargs = rule.getParameters();
      Collection pumpvars = pumpclause.getAllVariables();
      Collection midvars = midclause.getAllVariables();
      Collection bottomvars = bottomclause.getAllVariables();

      Variable parent = null;
      Variable child = null;
      Variable mid = null;

      Iterator it = midvars.iterator();
      while (it.hasNext()) {
        Variable var = (Variable) it.next();

        if (ruleargs.contains(var) &&
            !pumpvars.contains(var) &&
            bottomvars.contains(var) &&
            midvars.contains(var)) {
          if (child != null)
            return null; // ambiguous variable
          child = var;
        }

        if (!ruleargs.contains(var) &&
            !bottomvars.contains(var) &&
            midvars.contains(var) &&
            pumpvars.contains(var)) {
          if (mid != null)
            return null; // ambiguous variable
          mid = var;
        }
      }

      if (child == null || mid == null)
        return null; // couldn't find necessary vars

      it = pumpvars.iterator();
      while (it.hasNext()) {
        Variable var = (Variable) it.next();

        if (ruleargs.contains(var) &&
            pumpvars.contains(var) &&
            bottomvars.contains(var) &&
            !midvars.contains(var)) {
          if (parent != null)
            return null; // ambiguous variable
          parent = var;
        }
      }

      if (parent == null)
        return null; // couldn't find parent

      // ok, we are done. now we can create the optimized version
      return new HierarchyWalkerRulePredicate(rule, parent, child, mid, bottomclause);
    }

    /**
     * INTERNAL: Removes the 'remove-duplicates' predicate from the
     * lists, since this interferes with the logic that is trying to
     * work out whether or not we can apply this optimization.
     */
    private List filter(List clauses) {
      List filtered = new ArrayList();
      for (int ix = 0; ix < clauses.size(); ix++) {
        Object clause = clauses.get(ix);
        if (!(clause instanceof PredicateClause) ||
            !(((PredicateClause) clause).getPredicate() instanceof RemoveDuplicatesPredicate))
          filtered.add(clause);
      }
      return filtered;
    }
  }

  // ===== STRING PREFIX SEARCHES ===============================================

  // TODO
  //  1) implement isBoundAt
  //  2) generalize so variable can be SELECTed
  
  // Given a query containg the two following predicates
  //     pred1($A, $B)
  //     str:starts-with($B, $C)?

  // conditions for the optimization to kick in are
  //   1) $A must be unbound at pred1
  //   2) $B must be unbound at pred1
  //   3) $B must not be used anywhere else
  //   4) $C must be a literal
  //   5) pred1 must be dynamic occ predicate, 'resource', or 'value'

  // may have to strengthen #4 to say that $C must be a literal
  
  /**
   * INTERNAL: Optimizes queries that do lookup of occurrences by
   * string value, then filter the string value by a prefix.
   */
  public static class StringPrefixOptimizer extends AbstractQueryOptimizer {
  
    @Override
    public void optimize(TologQuery query, QueryContext context) {
      // find str:starts-with predicate
      PredicatePosition startsp = findPredicate(query.getClauses(),
                                    StringModule.StartsWithPredicate.class); 
      if (startsp == null)
        return;
      PredicateClause starts = startsp.getClause();
      
      // #5: find (dynamic occ | value | resource) predicate
      PredicatePosition valuep = findPredicate(query.getClauses(),
                                               DynamicOccurrencePredicate.class);
      if (valuep == null)
        return; // FIXME: extend to cover 'value' and 'resource'
      PredicateClause value = valuep.getClause();

      // #X: verify that predicates appear in same clause list
      if (startsp.getContainingList() != valuep.getContainingList())
        return;
      
      // #1: arg1 must be unbound at value
      Object arg1 = value.getArguments().get(0);
      if (!(arg1 instanceof Variable))
        return;
      if (isBoundAt(query.getClauses(), (Variable) arg1, value))
        return;

      // #2: arg2 must be unbound at value
      Object arg2 = value.getArguments().get(1);
      if (!(arg2 instanceof Variable))
        return;
      if (isBoundAt(query.getClauses(), (Variable) arg2, value))
        return;

      // starts must have arg2 as first argument
      if (!starts.getArguments().get(0).equals(arg2))
        return;

      // #3: arg2 must not be used anywhere else
      Collection users = findPredicatesUsing(query.getClauses(), (Variable) arg2);
      if (users.size() > 2)
        return;

      // #4: arg3 must be a literal
      Object arg3 = starts.getArguments().get(1);
      if (!(arg3 instanceof String))
        return;
      
      /// do the actual optimization
      // a) if arg2 is not in the SELECT list: change arg2 to be arg3
      if (!query.getSelectedVariables().contains(arg2))
        value.getArguments().set(1, arg3);
      
      // b) remove the starts clause
      removePredicate(query.getClauses(), starts);
      
      // c) inform value that it should do a prefix search
      value.addArgument(new PredicateOptions((String) arg3));
    }
  }

  // ===== HOISTING getRolesByType() INTO role-player ====================
  
  /**
   * INTERNAL: <p>Optimizes the role-player() predicate when the type of
   * the role is known from a type() predicate in the same query.
   * Typical usage is:</p>
   *
   * <pre>
   * role-player($R1, fixed-point),
   * association-role($A, $R1),
   * association-role($A, $R2),
   * $R1 /= $R2,
   * role-player($R2, $OTHER),
   * type($R1, required-type)?
   * </pre>
   *
   * <p>What's really needed for the optimization to kick in is two
   * things:</p>
   *
   * <ul>
   *   <li>In the role-player($A, $B) predicate $A must be unbound and
   *   $B must be bound
   *   <li>In the type($C, $D) predicate the $C must match $A, and $D
   *   must be bound <em>when the role-player() runs</em>, and <em>not
   *   by the type() predicate
   * </ul>
   */
  public static class AddTypeToRolePlayer extends AbstractQueryOptimizer {
  
    @Override
    public void optimize(TologQuery query, QueryContext context) {
      Iterator it = findPredicates(query.getClauses(),
                                   RolePlayerPredicate.class).iterator();
      while (it.hasNext()) {
        PredicatePosition rolepp = (PredicatePosition) it.next();
        optimize(query, rolepp);
      }
    }

    private void optimize(TologQuery query, PredicatePosition rolepp) {
      // --- role-player predicate
      // analyze predicate
      PredicateClause rolec = rolepp.getClause();

      Object arg1 = rolec.getArguments().get(0);
      if (!(arg1 instanceof Variable) ||
          isBoundAt(query.getClauses(), (Variable) arg1, rolec))
        return;

      Object arg2 = rolec.getArguments().get(1);
      if (arg2 instanceof Variable &&
          !isBoundAt(query.getClauses(), (Variable) arg2, rolec))
        return;      
      
      // --- type predicate
      PredicatePosition typepp = findTypePredicate(query, (Variable) arg1);
      if (typepp == null)
        return;
      
      PredicateClause typec = typepp.getClause();
      // INV: first arg of typec matches first of rolec

      Object arg4 = typec.getArguments().get(1);
      if (arg4 instanceof Variable &&
          !isBoundAt(query.getClauses(), (Variable) arg1, rolec))
        return;

      // FIXME: verify that rolec before typec in execution order
      if (rolepp.getContainingList() != typepp.getContainingList())
        return;
      
      // --- apply optimization

      // 1) send PredicateOptions to role-player()
      rolec.addArgument(new PredicateOptions(arg4));
      
      // 2) remove type predicate
      removePredicate(typepp.getContainingList(), typec);
    }

    private PredicatePosition findTypePredicate(TologQuery query, Variable arg1) {
      Iterator it = findPredicatesUsing(query.getClauses(), (Variable) arg1).
                      iterator();
      while (it.hasNext()) {
        PredicatePosition pp = (PredicatePosition) it.next();
        PredicateClause pc = pp.getClause();
        if (pc.getPredicate() instanceof TypePredicate &&
            arg1.equals(pc.getArguments().get(0)))
          return pp;
      }

      return null;
    }
  }

  // ===== INTERNAL UTILITIES ==================================================
  
  private static PredicatePosition findPredicate(List clauses, Class predicate) {
    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);

      if (clause instanceof PredicateClause) {
        PredicateClause pclause = (PredicateClause) clause;
        if (predicate.isInstance(pclause.getPredicate()))
          return new PredicatePosition(clauses, pclause);
        
      } else if (clause instanceof OrClause) {
        List alts = ((OrClause) clause).getAlternatives();
        for (int i = 0; i < alts.size(); i++)
          return findPredicate((List) alts.get(i), predicate);
            
      } else if (clause instanceof NotClause)
        return findPredicate(((NotClause) clause).getClauses(), predicate);
    }

    return null; // couldn't find it
  }

  // collection of PredicatePosition objects
  private static Collection findPredicates(List clauses, Class predicate) {
    List pps = new ArrayList();
    
    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);

      if (clause instanceof PredicateClause) {
        PredicateClause pclause = (PredicateClause) clause;
        if (predicate.isInstance(pclause.getPredicate()))
          pps.add(new PredicatePosition(clauses, pclause));
        
      } else if (clause instanceof OrClause) {
        List alts = ((OrClause) clause).getAlternatives();
        for (int i = 0; i < alts.size(); i++)
          pps.addAll(findPredicates((List) alts.get(i), predicate));
            
      } else if (clause instanceof NotClause)
        pps.addAll(findPredicates(((NotClause) clause).getClauses(), predicate));
    }

    return pps;
  }
  
  // WARN: doesn't support dynamic association predicates
  // returns collection of PredicatePosition objects
  private static Collection findPredicatesUsing(List clauses, Variable var) {
    Collection predicates = new ArrayList();
      
    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);

      if (clause instanceof PredicateClause) {
        PredicateClause pclause = (PredicateClause) clause;
        if (pclause.getArguments().contains(var))
          predicates.add(new PredicatePosition(clauses, pclause));
        
      } else if (clause instanceof OrClause) {
        List alts = ((OrClause) clause).getAlternatives();
        for (int i = 0; i < alts.size(); i++)
          predicates.addAll(findPredicatesUsing((List) alts.get(i), var));
            
      } else if (clause instanceof NotClause)
        predicates.addAll(findPredicatesUsing(((NotClause) clause).getClauses(),
                                              var));
    }

    return predicates;
  }

  private static boolean isBoundAt(List clauses, Variable var, PredicateClause pclause) {
    return isBoundAt(clauses, var, pclause, new CompactHashSet());
  }

  private static boolean isBoundAt(List clauses, Variable var,
                                   PredicateClause theclause, Set bound) {
      
    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);

      if (clause instanceof PredicateClause) {
        PredicateClause pclause = (PredicateClause) clause;
        if (pclause.equals(theclause))
          return bound.contains(var);
        bound.addAll(pclause.getAllVariables());
        
      } else if (clause instanceof OrClause) {
        List alts = ((OrClause) clause).getAlternatives();
        for (int i = 0; i < alts.size(); i++)
          if (isBoundAt((List) alts.get(i), var, theclause, bound))
            return true;
            
      } else if (clause instanceof NotClause)
        if (isBoundAt(((NotClause) clause).getClauses(), var, theclause, bound))
          return true;
    }

    return false;
  }    

  private static boolean removePredicate(List clauses, PredicateClause pclause) {
    // see if the clause is in this list, and if it is we are done
    if (clauses.contains(pclause)) {
      clauses.remove(pclause);
      return true;
    }
          
    // traverse
    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);

      if (clause instanceof OrClause) {
        List alts = ((OrClause) clause).getAlternatives();
        for (int i = 0; i < alts.size(); i++)
          if (removePredicate((List) alts.get(i), pclause))
            return true;
            
      } else if (clause instanceof NotClause)
        if (removePredicate(((NotClause) clause).getClauses(), pclause))
          return true;
    }

    return false;
  }
  
  // used to capture where the predicates came from
  static class PredicatePosition {
    private List clauses;
    private PredicateClause clause;
      
    public PredicatePosition(List clauses, PredicateClause clause) {
      this.clauses = clauses;
      this.clause = clause;
    }

    public List getContainingList() {
      return clauses;
    }

    public PredicateClause getClause() {
      return clause;
    }
  }

  // ===== NEXT IN SEQUENCE SEARCH =============================================
  
  // Given a query starting with:
  //     occurrence-type($TOPIC, $VALUE),
  //     $VALUE > %value%                  (any comparison, literal/parameter)
  //
  // where the query is sorted on $VALUE and there is a limit set, but no
  // offset,
  //
  // replace the beginning with:
  //     value-pumper($::OBJECT, $VALUE, %value%),
  //     type($::OBJECT, occurrence-type),
  //     occurrence($TOPIC, $::OBJECT)
  //
  // then look up the next values in sequence above %value% in batches
  // and pump them through until enough values have been found to
  // satisfy the limit.
  
  /**
   * INTERNAL: Optimizes queries that look for the next or the
   * previous value in a sequence from a given start value to not load
   * all values and then do it the hard way, but instead to use a
   * sorted index.
   */
  public static class NextPreviousOptimizer extends AbstractQueryOptimizer {
  
    @Override
    public void optimize(TologQuery query, QueryContext context) {

      // ===== CHECK IF OPTIMIZATION APPLIES
      
      // must have limit, must not have offset
      if (query.getLimit() == -1 || query.getOffset() != -1)
        return;

      // must have order by with a single variable
      List orderby = query.getOrderBy();
      if (orderby.size() != 1)
        return;
      Variable value = (Variable) orderby.get(0);

      // must start with dynamic occurrence predicate
      List clauses = query.getClauses();
      if (!(clauses.get(0) instanceof PredicateClause))
        return;

      PredicateClause dynocc = (PredicateClause) clauses.get(0);
      if (!(dynocc.getPredicate() instanceof DynamicOccurrencePredicate))
        return;
      DynamicOccurrencePredicate dynoccpred = (DynamicOccurrencePredicate)
        dynocc.getPredicate();
      
      // second predicate must be a comparison predicate
      if (clauses.size() < 2 ||
          !(clauses.get(1) instanceof PredicateClause))
        return;
      PredicateClause compar = (PredicateClause) clauses.get(1);
      PredicateIF comparpred = compar.getPredicate();
      if (!((comparpred instanceof GreaterThanPredicate) ||
            (comparpred instanceof GreaterThanEqualsPredicate) ||
            (comparpred instanceof LessThanPredicate) ||
            (comparpred instanceof LessThanEqualsPredicate)))
        return;

      // topic parameter to dynamic occurrence predicate must be open
      if (!(dynocc.getArguments().get(0) instanceof Variable))
        return;
      Variable topicvar = (Variable) dynocc.getArguments().get(0);

      // second parameter to dynamic occurrence predicate must be our value
      if (!dynocc.getArguments().get(1).equals(value))
        return;

      // one parameter to comparison predicate must be our value
      boolean literalIsFirst;
      Object literal = null;
      if (compar.getArguments().get(0).equals(value)) {
        literalIsFirst = false;
        literal = compar.getArguments().get(1);
      } else if (compar.getArguments().get(1).equals(value)) {
        literalIsFirst = true;
        literal = compar.getArguments().get(0);
      } else
        return;

      // other parameter must be a literal or a parameter
      if (!((literal instanceof String) ||
            (literal instanceof Parameter)))
        return;

      // ===== APPLY OPTIMIZATION

      // figure out what kind of comparison it is
      boolean bigger =
        (!literalIsFirst &&
         ((comparpred instanceof GreaterThanPredicate) ||
          (comparpred instanceof GreaterThanEqualsPredicate))) ||
        (literalIsFirst &&
         ((comparpred instanceof LessThanPredicate) ||
          (comparpred instanceof LessThanEqualsPredicate)));

      boolean equals = 
         (comparpred instanceof LessThanEqualsPredicate) ||
         (comparpred instanceof GreaterThanEqualsPredicate);

      // make new wrapper predicate
      Variable object = new Variable("::OBJECT");
      TopicIF type = dynoccpred.getType();
      PumpPredicate pumppred = new PumpPredicate(type.getTopicMap(), clauses,
                                                 query.getLimit(), value,
                                                 object, literal, equals,
                                                 bigger);
      List args = new ArrayList();
      args.add(literal);
      args.add(value);
      PredicateClause pump = new PumpClause(pumppred, args);
      List newclauses = new ArrayList();
      newclauses.add(pump);
      query.setClauseList(newclauses);

      // make type predicate clause
      PredicateIF typepred = new TypePredicate(type.getTopicMap());
      args = new ArrayList();
      args.add(object);
      args.add(type);
      PredicateClause typeclause = new PredicateClause(typepred, args);

      // make occurrence predicate clause
      PredicateIF occpred = new OccurrencePredicate(type.getTopicMap());
      args = new ArrayList();
      args.add(topicvar);
      args.add(object);
      PredicateClause occclause = new PredicateClause(occpred, args);

      // rewrite old clause list
      clauses.set(0, typeclause); // replace dynocc with type predicate
      clauses.set(1, occclause);  // replace comparison with occurrence predicate
    }
  }
  
  public static class PumpPredicate implements BasicPredicateIF {
    private OccurrenceIndexIF index;
    private List subclauses;
    private int limit;
    private Variable valuevar;
    private Variable objectvar;
    private Object literal;
    private boolean equals;
    private boolean bigger;

    public PumpPredicate(TopicMapIF topicmap, List subclauses, int limit,
                         Variable valuevar, Variable objectvar,
                         Object literal, boolean equals, boolean bigger) {
      this.index = (OccurrenceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.OccurrenceIndexIF");
      this.subclauses = subclauses;
      this.limit = limit;
      this.valuevar = valuevar;
      this.objectvar = objectvar;
      this.literal = literal;
      this.equals = equals;
      this.bigger = bigger;
    }
    
    @Override
    public String getName() {
      return "::pump-previous-next";
    }

    @Override
    public String getSignature() throws InvalidQueryException {
      return "s s";
    }

    @Override
    public int getCost(boolean[] boundparams) {
      // after the optimization, this is the only top-level predicate,
      // so what we return doesn't much matter. in any case, we'll
      // produce at most this.limit hits, so SMALL_RESULT seems fair
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    }

    @Override
    public QueryMatches satisfy(QueryMatches input, Object[] arguments)
      throws InvalidQueryException {

      int valueix = input.getIndex(valuevar);
      int objectix = input.getIndex(objectvar);
      int batchsize = limit * 5; // multiplied by 2 before we start
      QueryMatches result = new QueryMatches(input);
      Iterator it;
      String literalvalue;
      if (literal instanceof String)
        literalvalue = (String) literal;
      else 
        literalvalue = (String) input.data[0][input.getIndex(literal)];
      
      if (bigger)
        it = index.getValuesGreaterThanOrEqual(literalvalue);
      else
        it = index.getValuesSmallerThanOrEqual(literalvalue);
      while (result.last + 1 < limit && it.hasNext()) {
        // double batch size from previous try to avoid checking too many batches
        batchsize *= 2;
        
        // set up batch of new values
        QueryMatches batch = new QueryMatches(input);
        batch.ensureCapacity(batchsize);
        for (int ix = 0; ix < batchsize && it.hasNext(); ) {
          String value = (String) it.next();
          if (!equals && value.equals(literalvalue))
            continue;

          Iterator it2 = index.getOccurrences(value).iterator();
          for (; it2.hasNext(); ix++) {
            OccurrenceIF occ = (OccurrenceIF) it2.next();
            if (batch.last+1 == batch.size)
              batch.increaseCapacity();
            
            Object[] newrow = (Object[]) input.data[0].clone();
            batch.data[++batch.last] = newrow;
            newrow[valueix] = value;
            newrow[objectix] = occ;
          }
        }

        // pump it through the subclauses
        batch = AbstractQueryProcessor.satisfy(subclauses, batch);

        // add output rows to result
        result.add(batch);
      }

      return result;
    }
  }

  public static class PumpClause extends PredicateClause {
    public PumpClause(PredicateIF predicate, List arguments) {
      super(predicate, arguments);
    }
    
    @Override
    public List getArguments() {
      Collection items = new CompactHashSet(arguments);

      List clauses = ((PumpPredicate) predicate).subclauses;
      for (int ix = 0; ix < clauses.size(); ix++) {
        AbstractClause clause = (AbstractClause) clauses.get(ix);
        items.addAll(clause.getArguments());
      }
    
      List list = new ArrayList();
      list.addAll(items);
      return list;
    }
  }
}

// UNUSED OPTIMIZATION OPPORTUNITIES

// Had to make this optimization manually. WHY?
// <logic:set name="parents">
//   <tm:tolog query="selected(%product% : product, $CHILD : feature),
//+                   is-version-of($CHILDV : version, $CHILD : versioned),
//                    parent-of($PARENT, $CHILDV),
//                    is-version-of($PARENTV : version, $PARENT : versioned),
//                    included-in($PARENTV : feature-node, %version% : platform-version),
//-                   is-version-of($CHILDV : version, $CHILD : versioned),
//                    included-in($CHILDV : feature-node, %version% : platform-version),
//                    not(selected(%product% : product, $PARENT : feature))?"
//             rulesfile="/rules.tl"/>

// had to move is-feature() to after is-version-of manually. Shouldn't
// be necessary.
//     String query =
//       "value-like($TN, \"" + search + "\"), " +
//       "topic-name($VERSION, $TN), " +
//       "value($TN, $NAME), " +
//       "is-version-of($VERSION : version, $ABSTRACT : versioned), " +
//       "is-feature($ABSTRACT), " +
//       "$VERSION /= %feature%, " +
//       "included-in($VERSION : feature-node, %current-version% : platform-version) " +
//       "order by $VERSION?";


// replace variables with literals where possible

//   base-locator($A)?
//   subject-identifier($A, "http:/....")?

// common clause can be lifted out
//
//    select $CITY, count($OPERA) from        
//      { instance-of($CITY, city), 
//        premiere($OPERA : opera, $CITY : place) 
//      | instance-of($CITY, city), 
//        located-in($THEATRE : containee, $CITY : container),
//        premiere($OPERA : opera, $THEATRE : place) 
//      } 
//    order by $OPERA desc?

// ditto for early version of commentsquery on teacher-search.jsp

// the two topic() clauses can be removed
// the query should start with userownstopic
// the not-equals should be done immediately afterwards
//   String query = "select $TOPIC1, $TOPIC2 from " +
//                      "  value($n1, $value), " +
//                      "  value($n2, $value), " +
//                      "  $n1 /= $n2, " +
//                      "  topic-name($TOPIC1, $n1), " +
//                      "  topic-name($TOPIC2, $n2), " +
//                      "  topic($TOPIC1), " + 
//                      "  topic($TOPIC2), " +
//                      "  $TOPIC1 /= $TOPIC2, " +
//                      "  userownstopic($TOPIC1 : ownedtopic, @" + user.getObjectId() + " : bruker), " +
//                      "  userownstopic($TOPIC2 : ownedtopic, @" + user.getObjectId() + " : bruker)?";

// map automatically to dynamic occurrence predicate
//   occurrence($A, $O), type($O, rekkefolge), value($O, $VALUE)
//   ->
//   rekkefolge($A, $VALUE)


// redundant use of predicates for "typing"
//
//   topic($T), 
//   topic-name($T, $SCOPED),
//   scope($SCOPED, $SCOPE1)?


// ===== ESSAY =======================================================
// Nokia has a problem that deleting feature groups takes ages and ages,
// and this causes all sorts of difficulties on the system. We need to
// handle those difficulties at some stage, but for now, the simple fix
// is to speed up the deletion of feature groups.

// The problem is this query from the DeleteFeature action:

//     // dissociate feature and descendants from current version
//     query =
//       "import \"rules.tl\" as fm " +
//       "import \"http://psi.ontopia.net/tolog/experimental/\" as exp " +
//       "select $ASSOC from " +
//       "  { fm:descendant-v(%featurev%, $NODE, %pv%) | exp:in($NODE, %featurev%) }, " +
//       "  role-player($R1, $NODE), type($R1, feature-node), " +
//       "  association-role($ASSOC, $R1), type($ASSOC, included-in), " +
//       "  association-role($ASSOC, $R2), type($R2, platform-version), " +
//       "  role-player($R2, %pv%)?";
//     helper.delete(query);

// The optimizer decides (wrongly) that the type() predicate should go
// first, which means that the rule gets a huge result set to operate on
// when it starts, and this makes it very slow, since it has to do the
// same things over and over again.

// It's struck me before that on occasion developers will know that they
// are smarter than the optimizer, and so they should be able to override
// it. The question is how? 

// So I came up with this:

//     // dissociate feature and descendants from current version
//     query =
//       "/* #OPTION: optimizer.reorder = false */ " + // UNDOCUMENTED MAGIC... :-(
//       "import \"rules.tl\" as fm " +
//       "import \"http://psi.ontopia.net/tolog/experimental/\" as exp " +
//       "select $ASSOC from " +
//       "  { fm:descendant-v(%featurev%, $NODE, %pv%) | exp:in($NODE, %featurev%) }, " +
//       "  role-player($R1, $NODE), type($R1, feature-node), " +
//       "  association-role($ASSOC, $R1), type($ASSOC, included-in), " +
//       "  association-role($ASSOC, $R2), type($R2, platform-version), " +
//       "  role-player($R2, %pv%)?";
//     helper.delete(query);

// That would solve the problem for Nokia. Thoughts on this approach?
// (I'm only doing this for Nokia, and not checking it in just yet,
// anyway, so we can still change this around or take a different
// approach entirely.)


// (Below this point I'm mostly thinking out loud, but doing so helps
// me. Read it if you're interested.)

// On a different note: what struck me while writing this is that this
// problem could be avoided if tolog were a bit smarter. I'll try to
// explain. If you run the query with the type predicate first, then the
// fm:descendant-v predicate, it will work like this:

// Step 1, result set before type(...):

//   +------+------+----
//   | NODE | R1   | ...
//   +------+------+----
//   | null | null | ...
//   +------+------+----
  
// Step 2, result set after type(...), and before fm:descendant-v(...)

//   +------+------+----
//   | NODE | R1   | ...
//   +------+------+----
//   | null | r1   | ...
//   +------+------+----
//   | null | r2   | ...
//   +------+------+----
//   | null | r3   | ...
//   +------+------+----
//   | null | r4   | ...
//   +------+------+----
//   | ...  | ...  | ...

// And so on, for quite a few rows downwards.

// Now, there are two important things to observe here:

//   1) fm:descendant-v(...) doesn't actually *use* the R1 column, so the
//      fact that we've got a zillion different values here doesn't
//      affect it; fm:descendant-v(...) is going to do its thing on the
//      NODE column, over and over again repeating the same operation on
//      an unbound value

//   2) fm:descendant-v(...) is a rule, which means that it will start by
//      translating the result set from step 2 into an internal result
//      set, then do its thing, then translate it back to an external
//      result set

// In other words, if fm:descendant-v(...) collapsed the duplicate rows
// in the internal result set before doing its thing, then did a
// cartesian product when creating the external result at the end we
// would avoid repeating the same calculation thousands of times. The
// question is, I suppose, how to know when this is a useful thing to do,
// and when it would just be a waste of time. It might be that we want to
// add an additional optimizer for this case.



// ======================================================================

// denne tar 6500 msec
// select $FELTVERDI from
//   har-asstype-rep(%FELT%: objekt, $ASSTYPE: feltverdi),
//   type($ASSOSIASJON, $ASSTYPE),
//   type($ROLLE1, objekt),
//   role-player($ROLLE1, %OBJEKT%),
//   association-role($ASSOSIASJON, $ROLLE1),
//   association-role($ASSOSIASJON, $ROLLE2),
//   role-player($ROLLE2, $FELTVERDI),
//   $ROLLE1 /= $ROLLE2
// order by $FELTVERDI?

// denne tar 80 msec
// select $FELTVERDI from
//   har-asstype-rep(%FELT%: objekt, $ASSTYPE: feltverdi),
//   type($ASSOSIASJON, $ASSTYPE),
//   role-player($ROLLE1, %OBJEKT%),
//   association-role($ASSOSIASJON, $ROLLE1),
//   association-role($ASSOSIASJON, $ROLLE2),
//   role-player($ROLLE2, $FELTVERDI),
//   $ROLLE1 /= $ROLLE2
// order by $FELTVERDI? 

// forskjellen er at "type($ROLLE1, objekt)" er fjernet. grunnen til
// at dette gjoer saa stor forskjell er at den kjoeres foer linjen etter,
// som binder $ROLLE1 til mye faerre verdier. derfor skulle
// role-player() kjoert foer type(), men det gjoer den ikke. jaja.

// ==================================================

// |   select $TYPE, $TOPIC, $ID from
// |   {
// |     topic-name($TOPIC, $NAME), value-like($NAME, %SEARCHITEM%) |
// |     occurrence($TOPIC, $OCC),  value-like($OCC, %SEARCHITEM%)  |
// |     topic-name($TOPIC, $TN), variant($TN, $V), value-like($V, %SEARCHITEM%)
// |   }, 
// |   instance-of($TOPIC, $TYPE),
// |   object-id($TYPE, $ID)
// |   order by $TYPE, $TOPIC?
// | 
// | I made several experiments in the Omnigator, which I did not made
// | before I wrote this support ticket (sorry!), and I found out that
// | the "instance-of" and "object-id" make the query slow (betwenn two
// | and four seconds).
// 
// I think I see what your problem is. Most likely the optimizer screws
// this query up for you and runs it like this
// 
//   instance-of
//   object-id
//   { OR clause }
// 
// with the result being that you get all instances of all topic types,
// and then do the full-text search three times (once in each OR branch)
// for each instance/type combination.

// IDEA: evaluate OR clauses on the worst of the first clauses inside
//       each OR branch. would here keep the OR first

// ==================================================

// reordering screws this one up somehow

//     direct-instance-of(%OBJEKT%, $OBJEKTKLASSE),
//     arver-felt-fra($OBJEKTKLASSE : objekt, $FELTKILDE : feltkilde),
//     role-player($ROLLE1, %OBJEKT%),
//     association-role($ASSOSIASJON, $ROLLE1),
//     association-role($ASSOSIASJON, $ROLLE2),
//     role-player($ROLLE2, $FELTKILDEINSTANS),
//     $FELTKILDEINSTANS /= %OBJEKT%,
//     direct-instance-of($FELTKILDEINSTANS, $FELTKILDE)?

// ==================================================

// reordering also screws this one up

//     select $GRUPPE, $FELT, $TYPE, $FV, $FORMAT, $ELEMGRUPPENAVN, $ELEMNAVN, $SKJULT, $GRUPPESORTERING, $FELTSORTERING from
//     {
//       direct-instance-of(%OBJEKT%, $OBJEKTKLASSE),
//       {
//         har-felt($OBJEKTKLASSE : objekt, $FELT : feltverdi)
//       |
//         har-arvelig-felt(%FELTKILDEINSTANS% : objekt, $FELT : feltverdi)
//       }
//     |
//       har-formatversjon(%OBJEKT% : publikasjon, $FV : formatversjon),
//       har-format($FV : objekt, $FORMAT : feltverdi),
//       direct-instance-of($FV, formatversjon),
//       {
//         har-felt(formatversjon : objekt, $FELT : feltverdi)
//       |
//         har-arvelig-felt($FORMAT : objekt, $FELT : feltverdi)
//       }
//     },
//     har-feltgruppe($FELT : objekt, $GRUPPE : feltverdi),
//     har-felttype($FELT : objekt, $TYPE : feltverdi),
//     xmltag($FELT, $ELEMNAVN),
//     { xmlgruppe($FELT, $ELEMGRUPPENAVN) },
//     { er-skjult-felt($FELT : objekt, $SKJULT : feltverdi) },
//     { feltrekkefolge($GRUPPE, $GRUPPESORTERING) },
//     { feltrekkefolge($FELT, $FELTSORTERING) }
//     order by $GRUPPESORTERING, $FELTSORTERING ?

// ==================================================

// screwed up by reordering

//       select $GRUPPE, $FELT, $TYPE, $BESKRIVELSE, $GRUPPESORTERING, $FELTSORTERING from
//       direct-instance-of(%OBJEKT%, $OBJEKTKLASSE),
//       {
//         har-felt($OBJEKTKLASSE : objekt, $FELT : feltverdi)
//         |
//         arver-felt-fra($OBJEKTKLASSE : objekt, $FELTKILDE : feltkilde),
//         role-player($ROLLE1, %OBJEKT%),
//         association-role($ASSOSIASJON, $ROLLE1),
//         association-role($ASSOSIASJON, $ROLLE2),
//         role-player($ROLLE2, $FELTKILDEINSTANS),
//         $FELTKILDEINSTANS /= %OBJEKT%,
//         direct-instance-of($FELTKILDEINSTANS, $FELTKILDE),
//         har-arvelig-felt($FELTKILDEINSTANS : objekt, $FELT : feltverdi)
//       },
//       har-feltgruppe($FELT : objekt, $GRUPPE : feltverdi),
//       har-felttype($FELT : objekt, $TYPE : feltverdi),
//       { beskrivelse($FELT, $BESKRIVELSE) },
//       { feltrekkefolge($GRUPPE, $GRUPPESORTERING) },
//       { feltrekkefolge($FELT, $FELTSORTERING) }
//       order by $GRUPPESORTERING, $FELTSORTERING ?

// ==================================================

// broken by reordering

//     select $FELTVERDI, $ASSOSIASJONSTYPE from
//     har-asstype-rep(%FELT% : objekt, $ASSOSIASJONSTYPE : feltverdi),
//     role-player($ROLLE1, %OBJEKT%),
//     association-role($ASSOSIASJON, $ROLLE1),
//     type($ASSOSIASJON, $ASSOSIASJONSTYPE),
//     association-role($ASSOSIASJON, $ROLLE2),
//     role-player($ROLLE2, $FELTVERDI),
//     $ROLLE1 /= $ROLLE2
//     order by $ASSOSIASJONSTYPE, $FELTVERDI?

// ==================================================

// reordering doesn't see what best choice is

// using FTM for i"http://cch.com/xtm/fedtax/#"
// instance-of($DOC, FTM:document), 
// FTM:normval($DOC, "2790-New"),
// FTM:pub($DOC, "MTG")?

// the instance-of gives some 100k topics
// the normval gives 3

// ==================================================

// switching order of roles in Falls_under takes query time from
// 3.6 sec to 0.12 secs...

// using dlo for i"http://test.greenwood.com/DLOXTM/"
// Select $CONTENT, $CONTENTID from
//   dlo:Available_in(@47677 : dlo:MODULE, $CONTENT : dlo:CONTENT_OBJECT),
//   dlo:Falls_under($CONTENT : dlo:CONTENT_OBJECT, @92153 : dlo:HEADING),
//   dlo:contentID($CONTENT, $CONTENTID),
//   not(instance-of($CONTENT, dlo:Related_Content_Object))
// order by $CONTENT  LIMIT 20?
