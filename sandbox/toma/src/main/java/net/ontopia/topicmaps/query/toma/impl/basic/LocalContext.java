package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.HashMap;
import java.util.List;

import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: The current context when evaluating a query.
 */
public class LocalContext implements Cloneable {

  private TopicMapIF topicmap;
  private HashMap<String, ResultSet> resultsets;

  public LocalContext(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    this.resultsets = new HashMap<String, ResultSet>();
  }

  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  public void addResultSet(ResultSet resultset) {
    List<String> variables = resultset.getBoundVariables();
    for (String var : variables) {
      resultsets.put(var, resultset);
    }
  }

  public ResultSet getResultSet(String boundVariable) {
    return resultsets.get(boundVariable.toUpperCase());
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Object clone() throws CloneNotSupportedException {
    LocalContext c = new LocalContext(this.topicmap);
    c.resultsets = (HashMap<String, ResultSet>) this.resultsets.clone();
    return c;
  }
}
