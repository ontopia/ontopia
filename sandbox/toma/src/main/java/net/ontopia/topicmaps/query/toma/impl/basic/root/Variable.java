package net.ontopia.topicmaps.query.toma.impl.basic.root;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicRootIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
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
