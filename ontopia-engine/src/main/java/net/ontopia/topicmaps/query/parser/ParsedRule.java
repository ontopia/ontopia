
package net.ontopia.topicmaps.query.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.QueryAnalyzer;

/**
 * INTERNAL: Represents a parsed rule.
 */
public class ParsedRule {
  private String name;
  private List parameters;
  private List clauses;
  private Map typemap;
  private TologOptions options;
  
  public ParsedRule(String name) {
    this.name = name;
  }

  public void init(TologOptions options) {
    this.options = options;
    this.parameters = new ArrayList();
    this.clauses = new ArrayList();
  }

  public boolean initialized() {
    return options != null;
  }

  public String getName() {
    return name;
  }

  public List getClauses() {
    return clauses;
  }

  public List getParameters() {
    return parameters;
  }

  public void setClauseList(List clauses) {
    this.clauses = clauses;
  }
  
  public void addParameter(Variable var) {
    parameters.add(var);
  }

  public Map getVariableTypes() {
    return typemap;
  }

  public Map getParameterTypes() {
    return Collections.EMPTY_MAP;
  }

  public TologOptions getOptions() {
    // will only work after close() has been called
    return options;
  }

  /**
   * Some checks can only be performed when we know that we have
   * parsed the entire rule. Therefore close() is called once parsing
   * of the rule has ended. It verifies that all parameters to the
   * rule are actually used in the rule (no free variables), and runs
   * type inferencing on the rule.
   */
  public void close() throws InvalidQueryException {
    // verify that all parameters are used
    Set allVariables = new HashSet();
    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);
      allVariables.addAll(clause.getAllVariables());
    }
    for (int ix = 0; ix < parameters.size(); ix++) {
      Variable var = (Variable) parameters.get(ix);
      if (!allVariables.contains(var))
        throw new InvalidQueryException("Parameter " + var + " to rule " + name +
                                        " is not bound by the rule.");
    }

    // run type inferencing
    boolean strict = options.getBooleanValue("compiler.typecheck");
    typemap = QueryAnalyzer.analyzeTypes(clauses, strict).getVariableTypes();
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof ParsedRule) {
      ParsedRule other = (ParsedRule)obj;
      return (name.equals(other.name) &&
              parameters.equals(other.parameters) &&
              clauses.equals(other.clauses));
    }
    return false;
  }  
}
