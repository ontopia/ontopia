package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: Reifier path element in an path expression. Returns the reifier for
 * an topic map construct.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>NAME
 * <li>ASSOCIATION
 * <li>VARIANT
 * <li>OCCURRENCE
 * </ul>
 * </p><p>
 * <b>Output</b>: TOPIC
 * </p>
 */
public class ReifierPath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.NAME);
    inputSet.add(TYPE.VARIANT);
    inputSet.add(TYPE.OCCURRENCE);
    inputSet.add(TYPE.ASSOCIATION);
  }

  public ReifierPath() {
    super("REIFIER");
  }

  protected boolean isLevelAllowed() {
    return false;
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
  
  public Collection<TopicIF> evaluate(LocalContext context, Object input) {
    ReifiableIF construct = (ReifiableIF) input;
    Collection<TopicIF> coll = new LinkedList<TopicIF>();
    coll.add(construct.getReifier());
    return coll;
  }
}
