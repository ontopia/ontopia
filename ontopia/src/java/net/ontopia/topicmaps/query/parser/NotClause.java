
// $Id: NotClause.java,v 1.7 2005/07/13 08:57:21 grove Exp $

package net.ontopia.topicmaps.query.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.ontopia.utils.StringUtils;

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
