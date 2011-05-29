
package net.ontopia.topicmaps.query.impl.utils;

import java.util.Map;

import net.ontopia.topicmaps.query.parser.ParsedRule;
import net.ontopia.topicmaps.query.parser.TologQuery;
import net.ontopia.topicmaps.query.parser.TologOptions;

/**
 * INTERNAL: Used during traversal of queries to represent the context
 * at any given point in the query.
 */
public class QueryContext {
  private int nesting_level;
  private ParsedRule rule;  // not set outside rules
  private TologQuery query;

  // note that query may be null (in DeclContext, for example)
  public QueryContext(TologQuery query, ParsedRule rule) {
    this.query = query;
    this.rule = rule;
  }
  
  public QueryContext(TologQuery query) {
    this.query = query;
  }

  /**
   * Returns value of boolean option.
   */
  public boolean getBooleanOption(String name) {
    TologOptions options;
    if (query != null)
      options = query.getOptions();
    else
      options = rule.getOptions();

    return options.getBooleanValue(name);
  }

  /**
   * Returns the name of the rule we are traversing. If we are
   * traversing a query (that is, not a rule at all) null is returned.
   */
  public String getRuleName() {
    if (rule == null)
      return null;
    else
      return rule.getName();
  }
  
  /**
   * Returns the clause list nesting level we are at. The top level of
   * a query or rule is 1.
   */
  public int getNestingLevel() {
    return nesting_level;
  }

  public Map getVariableTypes() {
    if (rule != null)
      return rule.getVariableTypes();
    else
      return query.getVariableTypes();
  }

  public Map getParameterTypes() {
    if (rule != null)
      return rule.getParameterTypes();
    else
      return query.getParameterTypes();
  }  
  
  public void enterClauseList() {
    nesting_level++;
  }

  public void leaveClauseList() {
    nesting_level--;
  }
  
}
