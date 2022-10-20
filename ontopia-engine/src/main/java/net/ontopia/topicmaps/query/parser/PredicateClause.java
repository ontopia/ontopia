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

  @Override
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

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(predicate.getName() + "(");
    for (int ix = 0; ix < arguments.size(); ix++) {
      if (arguments.get(ix) == null) {
        buf.append("<null>");
      } else {
        buf.append(arguments.get(ix).toString());
      }
      if (ix+1 < arguments.size()) {
        buf.append(", ");
      }
    }
    buf.append(')');
    return buf.toString();
  }

  @Override
  public Collection getAllVariables() {
    List vars = new ArrayList();
    
    for (int i = 0; i < arguments.size(); i++) {
      Object argument = arguments.get(i);
      if (argument instanceof Variable) { 
        vars.add(argument);
      } else if (argument instanceof Pair) {
        Object arg = ((Pair) argument).getFirst();
        if (arg instanceof Variable) {
          vars.add(arg);
        }
      }
    }

    return vars;
  }

  @Override
  public Collection getAllLiterals() {
    List literals = new ArrayList();
    
    for (int i = 0; i < arguments.size(); i++) {
      Object argument = arguments.get(i);
      if (argument instanceof Pair) {
        argument = ((Pair) argument).getFirst();
      }
      if (!(argument instanceof Variable)) {
        literals.add(argument);
      }
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
        ((RulePredicate) predicate).replaceable()) {
      return ((RulePredicate) predicate).translate(arguments);
    }
    return this;
  }
}
