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
 * INTERNAL: Used to represent not clauses in tolog queries.
 */
public class NotClause extends AbstractClause {
  protected List clauses;
  
  public NotClause() {
    clauses = new ArrayList();
  }
  
  public NotClause(List clauses) {
    this.clauses = clauses;
  }

  public void setClauseList(List clauses) {
    this.clauses = clauses;
  }

  public List getClauses() {
    return clauses;
  }

  public Collection getAllVariables() {
    Collection vars = new HashSet();

    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);
      vars.addAll(clause.getAllVariables());
    }
    
    return vars;
  }

  public Collection getAllLiterals() {
    Collection literals = new HashSet();

    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);
      literals.addAll(clause.getAllLiterals());
    }
    
    return literals;
  }
  
  public List getArguments() {
    Collection items = new HashSet();

    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);
      items.addAll(clause.getArguments());
    }
    
    List list = new ArrayList();
    list.addAll(items);
    return list;
  }
  
  public String toString() {
    return "not(" + StringUtils.join(clauses, ", ") + ")";
  }
  
}
