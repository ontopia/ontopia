package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.Level;

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
  public Collection<TopicIF> evaluate(LocalContext context, Object input) {
    TopicIF topic = (TopicIF) input;
    
    // level is required for this element
    Level l = getLevel();
    
    // use a set as collection for the types, as one type can occur multiple
    // times (and should only be counted once).
    Collection<TopicIF> types = new HashSet<TopicIF>();

    // include the topic itself, if the start level is 0
    if (l.getStart() == 0) {
      types.add(topic);
    }

    ClassInstanceIndexIF index = (ClassInstanceIndexIF) context.getTopicMap()
        .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    fillTypeList(index.getTopics(topic), l, 1, types, index);
    return types;
  }
  
  @SuppressWarnings("unchecked")
  private void fillTypeList(Collection<TopicIF> types, Level l, int depth,
      Collection<TopicIF> result, ClassInstanceIndexIF index) {
    // we are finished if we reached the end of the range, or the typelist is empty
    if (depth > l.getEnd() || types == null) {
      return;
    }

    // only add the types if we are already within the required range
    if (depth >= l.getStart()) {
      result.addAll(types);
    }
    
    for (TopicIF type : types) {
      fillTypeList(index.getTopics(type), l, depth+1, result, index);
    }
  }
}
