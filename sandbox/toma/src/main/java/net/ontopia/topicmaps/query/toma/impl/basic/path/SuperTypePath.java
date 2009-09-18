package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: Supertype path element in an path expression. Returns all supertypes 
 * of a given type from the topic map. 
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * </ul>
 * </p><p>
 * <b>Output</b>: TOPIC
 * </p>
 */
public class SuperTypePath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }
  
  public SuperTypePath() {
    super("SUPER");
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
  
  public Collection<TopicIF> evaluate(LocalContext context, Object input)
      throws InvalidQueryException {
    // TODO: implement supertype functionality, currently just return an empty
    // list
    return new LinkedList<TopicIF>();
  }
}
