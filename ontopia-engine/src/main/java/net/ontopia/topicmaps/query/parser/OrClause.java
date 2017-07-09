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
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: Used to represent or clauses in tolog queries.
 */
public class OrClause extends AbstractClause {
  protected List alternatives;
  protected boolean shortcircuit;
  public OrClause() {
    alternatives = new ArrayList();
  }

  public OrClause(List alternatives) {
    this.alternatives = alternatives;
  }

  public boolean getShortCircuit() {
    return shortcircuit;
  }

  public void setShortCircuit(boolean shortcircuit) {
    this.shortcircuit = shortcircuit;
  }

  public void addClauseList(List alternative) {
    alternatives.add(alternative);
  }

  public List getAlternatives() {
    return alternatives;
  }

  public Collection getAllVariables() {
    Collection vars = new HashSet();

    for (int ix = 0; ix < alternatives.size(); ix++) {
      List subclauses = (List) alternatives.get(ix);
      
      for (int i = 0; i < subclauses.size(); i++) {
        AbstractClause clause = (AbstractClause) subclauses.get(i);
        vars.addAll(clause.getAllVariables());
      }
    }
    
    return vars;
  }

  public Collection getAllLiterals() {
    Collection literals = new HashSet();

    for (int ix = 0; ix < alternatives.size(); ix++) {
      List subclauses = (List) alternatives.get(ix);
      
      for (int i = 0; i < subclauses.size(); i++) {
        AbstractClause clause = (AbstractClause) subclauses.get(i);
        literals.addAll(clause.getAllLiterals());
      }
    }
    
    return literals;
  }
  
  public List getArguments() {
    List args = new ArrayList();

    for (int ix = 0; ix < alternatives.size(); ix++) {
      List subclauses = (List) alternatives.get(ix);
      
      for (int i = 0; i < subclauses.size(); i++) {
        AbstractClause clause = (AbstractClause) subclauses.get(i);
        args.addAll(clause.getArguments());
      }
    }
    
    return args;
  }
  
  public String toString() {
    return "{" + StringUtils.join(alternatives, (shortcircuit ? " || " : " | ")) + "}";
  }
  
}
