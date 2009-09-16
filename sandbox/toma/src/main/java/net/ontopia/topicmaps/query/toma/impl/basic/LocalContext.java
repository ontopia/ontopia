package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapIF;

public class LocalContext implements Cloneable {

  private TopicMapIF topicmap;
  private Map<String, ResultSet> resultsets;

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
}
