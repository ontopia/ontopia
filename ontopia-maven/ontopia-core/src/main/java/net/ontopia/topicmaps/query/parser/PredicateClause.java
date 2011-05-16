
// $Id: PredicateClause.java,v 1.9 2005/07/13 08:57:21 grove Exp $

package net.ontopia.topicmaps.query.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ontopia.topicmaps.query.impl.basic.RulePredicate;

/**
 * INTERNAL: Used to represent clauses in tolog queries.
 */
public class PredicateClause extends AbstractClause {
  protected PredicateIF predicate;
  protected List arguments;

  public PredicateClause() {
    // used when predicate can be a dynamic predicate, and we need to
    // investigate the arguments to know what predicate it is
    arguments = new ArrayList();
  }
  
  public PredicateClause(PredicateIF predicate) {
    this.predicate = predicate;
    this.arguments = new ArrayList();
  }
  
  public PredicateClause(PredicateIF predicate, List arguments) {
    this.predicate = predicate;
    this.arguments = arguments;
  }

  public List getArguments() {
    return arguments;
  }

  public PredicateIF getPredicate() {
    return predicate;
  }

  // WARN: use only if you know what you are doing
  public void setPredicate(PredicateIF predicate) {
    this.predicate = predicate;
  }

  public void addArgument(Object object) {
    arguments.add(object);
  }

  public String toString() {
    StringBuffer buf = new StringBuffer(predicate.getName() + "(");
    for (int ix = 0; ix < arguments.size(); ix++) {
      if (arguments.get(ix) == null)
        buf.append("<null>");
      else
        buf.append(arguments.get(ix).toString());
      if (ix+1 < arguments.size())
        buf.append(", ");
    }
    buf.append(")");
    return buf.toString();
  }

  public Collection getAllVariables() {
    List vars = new ArrayList();
    
    for (int i = 0; i < arguments.size(); i++) {
      Object argument = arguments.get(i);
      if (argument instanceof Variable) 
        vars.add(argument);
      else if (argument instanceof Pair) {
        Object arg = ((Pair) argument).getFirst();
        if (arg instanceof Variable)
          vars.add(arg);
      }
    }

    return vars;
  }

  public Collection getAllLiterals() {
    List literals = new ArrayList();
    
    for (int i = 0; i < arguments.size(); i++) {
      Object argument = arguments.get(i);
      if (argument instanceof Pair)
        argument = ((Pair) argument).getFirst();
      if (!(argument instanceof Variable)) 
        literals.add(argument);
    }

    return literals;
  }
  
  /**
   * INTERNAL: Returns an equivalent, but more efficient, clause, if
   * such a clause is possible; if not returns itself. Used by the
   * optimizer to optimize queries.
   */
  public PredicateClause getReplacement() {
    if (predicate instanceof RulePredicate &&
        ((RulePredicate) predicate).replaceable()) 
      return ((RulePredicate) predicate).translate(arguments);
    return this;
  }
}
