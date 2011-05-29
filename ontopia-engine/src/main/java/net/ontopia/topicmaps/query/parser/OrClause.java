
package net.ontopia.topicmaps.query.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.ontopia.utils.StringUtils;

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
