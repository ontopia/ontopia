package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractVariable;

public class Variable extends AbstractVariable implements BasicRootIF {

  public Variable(String name) {
    super(name.toUpperCase());
  }

  public Collection<?> evaluate(LocalContext context) {

    // try to get a ResultSet that already bound the variable
    ResultSet rs = context.getResultSet(toString());

    // TODO: Variables can be of any type, not just topics

    if (rs != null) {
      return rs.getValues(toString());
    } else {
      TopicMapIF topicmap = context.getTopicMap();
      return topicmap.getTopics();
    }
  }
}
