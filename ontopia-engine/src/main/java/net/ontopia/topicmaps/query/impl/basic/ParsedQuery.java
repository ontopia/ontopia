
package net.ontopia.topicmaps.query.impl.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.parser.TologQuery;
import net.ontopia.topicmaps.query.parser.Variable;

/**
 * INTERNAL: Class used to represent parsed queries. The class wraps a
 * query executer and a tolog query intance (as generated by the
 * parser). The actual query execution is delegated to the query
 * processor.
 */
public class ParsedQuery implements ParsedQueryIF {

  protected TologQuery query;
  protected QueryProcessor processor;
  
  public ParsedQuery(QueryProcessor processor, TologQuery query) {
    this.processor = processor;
    this.query = query;
  }

  public List getClauses() {
    return query.getClauses();
  }
  
  /// ParsedQueryIF implementation [the class does not implement the interface]
  
  public List<String> getSelectedVariables() {
    return getVariables(query.getSelectedVariables());
  }

  public Collection<String> getAllVariables() {
    return getVariables(query.getAllVariables());
  }
  
  public Collection<String> getCountedVariables() {
    return getVariables(query.getCountedVariables());
  }

  public List<String> getOrderBy() {
    return getVariables(query.getOrderBy());
  }

  public boolean isOrderedAscending(String name) {
    return query.isOrderedAscending(name);
  }

  public QueryResultIF execute() throws InvalidQueryException {
    return processor.execute(query);
  }

  public QueryResultIF execute(Map<String, ?> arguments) throws InvalidQueryException {
    return processor.execute(query, arguments);
  }
  
  /// Object implementation

  public String toString() {
    return query.toString();
  }

  protected List<String> getVariables(Collection<Variable> varnames) {
    List<String> results = new ArrayList<String>(varnames.size());
    Iterator<Variable> iter = varnames.iterator();
    while (iter.hasNext()) {
      results.add(iter.next().getName());
    }
    return results;
  }

  public Object[] getVariableTypes(String varname) {
    return (Object[]) query.getVariableTypes().get(varname);
  }  
}
