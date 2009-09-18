package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: Scope path element in an path expression. Returns all the scopes of 
 * a given topic map construct.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>NAME
 * <li>VARIANT
 * <li>OCCURRENCE
 * <li>ASSOCIATION
 * </ul>
 * </p><p>
 * <b>Output</b>: TOPIC
 * </p>
 */
public class ScopePath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.NAME);
    inputSet.add(TYPE.VARIANT);
    inputSet.add(TYPE.OCCURRENCE);
    inputSet.add(TYPE.ASSOCIATION);
  }
  
  public ScopePath() {
    super("SC");
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
  
  @SuppressWarnings("unchecked")
  public Collection<TopicIF> evaluate(LocalContext context, Object input) {
    ScopedIF scoped = (ScopedIF) input;
    return scoped.getScope();
  }
}
