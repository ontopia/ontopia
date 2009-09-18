package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: Instance path element in an path expression. Returns all topics
 * that are an instance of a given type.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * </ul>
 * </p><p>
 * <b>Output</b>: TOPIC
 * </p>
 */
public class InstancePath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }
  
  public InstancePath() {
    super("INSTANCE");
  }

  protected boolean isLevelAllowed() {
    return true;
  }

  protected boolean isScopeAllowed() {
    return false;
  }
  
  protected boolean isTypeAllowed() {
    return false;
  }

  protected boolean isChildAllowed() {
    return false;
  }

  public Set<TYPE> validInput() {
    return inputSet;
  }
  
  public TYPE output() {
    return TYPE.TOPIC;
  }

  @SuppressWarnings("unchecked")
  public Collection<?> evaluate(LocalContext context, Object input) {
    ClassInstanceIndexIF index = (ClassInstanceIndexIF) context.getTopicMap()
        .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    Collection<TopicIF> instances = index.getTopics((TopicIF) input);
    return instances;
  }  
}
