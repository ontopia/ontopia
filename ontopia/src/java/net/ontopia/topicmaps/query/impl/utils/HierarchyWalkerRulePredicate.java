
// $Id: HierarchyWalkerRulePredicate.java,v 1.5 2008/07/23 11:24:35 lars.garshol Exp $

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.impl.basic.RulePredicate;
import net.ontopia.topicmaps.query.impl.basic.QueryTracer;
import net.ontopia.topicmaps.query.parser.PredicateClause;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.utils.CompactHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 */
public class HierarchyWalkerRulePredicate implements BasicPredicateIF {
  private static Logger log =
    LoggerFactory.getLogger(HierarchyWalkerRulePredicate.class.getName());
  
  protected RulePredicate rule;
  protected Variable firstvar;
  protected Variable secondvar;
  protected Variable midvar;
  protected PredicateClause wrapped; // mid; the predicate being recursed over
  
  public HierarchyWalkerRulePredicate(RulePredicate rule,
                                      Variable firstvar,
                                      Variable secondvar,
                                      Variable midvar,
                                      PredicateClause wrapped) {
    this.rule = rule;
    this.firstvar = firstvar;
    this.secondvar = secondvar;
    this.midvar = midvar;
    this.wrapped = wrapped;
  }
  
  // --- PredicateIF implementation

  public String getName() {
    return rule.getName();
  }

  public String getSignature() throws InvalidQueryException {
    return rule.getSignature();
  }

  public int getCost(boolean[] boundparam) {
    return rule.getCost(boundparam);
  }
  
  // --- BasicPredicateIF implementation
  
  public QueryMatches satisfy(QueryMatches extmatches, Object[] extarguments)
    throws InvalidQueryException {

    // use wrapped rule if there are many matches already
    // FIXME: make this work for more matches
    if (extmatches.last > 0)
      return rule.satisfy(extmatches, extarguments);

    QueryTracer.trace("satisfying hierarchywalker " + getName(), extarguments);
    
    // find indexes of firstvar and secondvar
    int ix1 = -1; // firstvar index
    int ix2 = -1; // secondvar index
    List params = rule.getParameters();
    for (int ix = 0; ix < params.size(); ix++) {
      Object p = params.get(ix);
      if (p.equals(firstvar))
        ix1 = extmatches.getIndex(extarguments[ix]);
      else if (p.equals(secondvar))
        ix2 = extmatches.getIndex(extarguments[ix]);
    }

    // $A $B -> run using wrapped rule (may be slower; need to test)
    if (!extmatches.bound(ix1) && !extmatches.bound(ix2))
      return rule.satisfy(extmatches, extarguments);

    // $A b  -> use optimization
    // a  $B -> same
    // a  b  -> same

    log.debug("hierarchy-walker runs");
    
    // 1) get results of open query on predicate
    QueryMatches result = runPredicate(extmatches, extarguments);
    
    log.debug("table size: " + (result.last + 1));
    
    // 2) find transitive closure from starting object
    int startcol = ix1;
    int goalcol = ix2;
    if (!extmatches.bound(ix1)) {
      startcol = ix2;
      goalcol = ix1;
    }
    Object startval = extmatches.data[0][startcol];

    //  figure out columns in result
    int resstartcol = result.getIndex(firstvar);
    int resgoalcol = result.getIndex(secondvar);
    if (!extmatches.bound(ix1)) {
      resstartcol = result.getIndex(secondvar);
      resgoalcol = result.getIndex(firstvar);
    }
    
    Set closure = findTransitiveClosure(result, resstartcol, resgoalcol, startval);

    log.debug("closure found, size: " + closure.size());
    
    // 3) produce situation-specific response
    QueryMatches ownresult = new QueryMatches(extmatches);
    ownresult.ensureCapacity(closure.size());
    Iterator it = closure.iterator();
    for (int ix = 0; ix < closure.size(); ix++) {
      ownresult.data[++ownresult.last] = (Object[]) extmatches.data[0].clone();
      ownresult.data[ownresult.last][goalcol] = it.next();
    }

    QueryTracer.trace("finished hierarchywalker " + getName());
    
    return ownresult;
  }

  // --- Internal methods

  // orgargs: arguments to rule as a whole
  private QueryMatches runPredicate(QueryMatches extmatches, Object[] extargs)
    throws InvalidQueryException {
    // get ready to run wrapped predicate
    net.ontopia.topicmaps.query.impl.basic.QueryContext context =
      extmatches.getQueryContext();
    Collection items = rule.findClauseItems(rule.getClauses(),
                                            context.getParameters());
    QueryMatches matches = new QueryMatches(items, context);
    matches.last++; // make an empty row
    matches.insertConstants();

    // bind internal variables to external arguments
    for (int ix = 0; ix < extargs.length; ix++) {
      Variable intvar = (Variable) rule.getParameters().get(ix);
      if (intvar.equals(firstvar) ||
          intvar.equals(secondvar) ||
          intvar.equals(midvar))
        continue;

      if (extargs[ix] instanceof TMObjectIF || extargs[ix] instanceof String) {
        int col = matches.getIndex(intvar);
        matches.data[0][col] = extargs[ix];
      }
    }

    // run wrapped predicate
    BasicPredicateIF pred = (BasicPredicateIF) wrapped.getPredicate();
    return pred.satisfy(matches, wrapped.getArguments().toArray());
  }

  private Set findTransitiveClosure(QueryMatches result, int startcol,
                                    int goalcol, Object startval) {
    Set closure = new CompactHashSet(100);
    closure.add(startval);

    int before;
    do {
      before = closure.size();
      addTransitively(result, startcol, goalcol, closure);
    } while (before < closure.size());

    closure.remove(startval); // FIXME: this isn't a proper fix!
    
    return closure;
  }

  private void addTransitively(QueryMatches result, int startcol, int goalcol,
                               Set closure) {
    for (int ix = 0; ix <= result.last; ix++) {
      if (closure.contains(result.data[ix][startcol]))
        closure.add(result.data[ix][goalcol]);
    }
  }
}
