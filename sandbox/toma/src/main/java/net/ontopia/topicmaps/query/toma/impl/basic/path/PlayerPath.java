package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: Player path element in an path expression. Returns all players
 * of a given input association.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>ASSOCIATION
 * </ul>
 * </p><p>
 * <b>Output</b>: TOPIC
 * </p>
 */
public class PlayerPath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.ASSOCIATION);
  }
  
  public PlayerPath() {
    super("PLAYER");
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
    Collection<AssociationRoleIF> roles = assoc.getRoles();
    LinkedList<TopicIF> result = new LinkedList<TopicIF>();
    for (AssociationRoleIF role : roles) {
      result.add(role.getPlayer());
    }
    return result;
  }  
}
