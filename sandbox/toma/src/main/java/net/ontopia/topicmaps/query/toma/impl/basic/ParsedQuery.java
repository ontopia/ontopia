package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.toma.parser.ast.TomaQuery;

public class ParsedQuery implements ParsedQueryIF {

  private TomaQuery query;
  private BasicQueryProcessor processor;

  public ParsedQuery(BasicQueryProcessor processor, TomaQuery query) {
    this.processor = processor;
    this.query = query;
  }

  public QueryResultIF execute() throws InvalidQueryException {
    return processor.execute(query);
  }

  public QueryResultIF execute(Map<String, ?> arguments)
      throws InvalidQueryException {
    return execute();
  }

  public Collection<String> getAllVariables() {
    // FIXME: return all variables instead of selected
    return query.getSelectedVariables();
  }

  public Collection<String> getCountedVariables() {
    // FIXME: return only counted variables
    return query.getSelectedVariables();
  }

  public List<String> getOrderBy() {
    return query.getOrderByVariables();
  }

  public List<String> getSelectedVariables() {
    return query.getSelectedVariables();
  }

  public boolean isOrderedAscending(String name) {
    return false;
  }

  public String toString() {
    return query.getParseTree();
  }
}
