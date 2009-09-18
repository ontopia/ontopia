package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: Role path element in an path expression. Returns all roles for a
 * given input association.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>ASSOCIATION
 * </ul>
 * </p><p>
 * <b>Output</b>: TOPIC
 * </p>
 */
public class RolePath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.ASSOCIATION);
  }
  
  public RolePath() {
    super("ROLE");
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
    AssociationIF assoc = (AssociationIF) input;
    return assoc.getRoleTypes();
  }
}
