package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;

/**
 * INTERNAL: Association path element in an path expression. Returns all topics
 * that take part in the specified association.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * </ul>
 * </p>
 * <p>
 * <b>Output</b>: TOPIC
 * </p>
 */
public class AssocPath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;

  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
    inputSet.add(TYPE.NONE);
  }

  public AssocPath() {
    super("ASSOC");
  }

  protected boolean isLevelAllowed() {
    return false;
  }

  protected boolean isScopeAllowed() {
    return true;
  }

  protected boolean isTypeAllowed() {
    return true;
  }

  protected boolean isChildAllowed() {
    return true;
  }

  public Set<TYPE> validInput() {
    return inputSet;
  }

  public TYPE output() {
    return TYPE.TOPIC;
  }

  @SuppressWarnings("unchecked")
  public Collection<?> evaluate(LocalContext context, Object input)
      throws InvalidQueryException {

    ClassInstanceIndexIF index = (ClassInstanceIndexIF) context.getTopicMap()
        .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    Collection<TopicIF> validScopes = null;
    if (getScope() != null) {
      PathExpression scope = (PathExpression) getScope();
      ResultSet scopes = scope.evaluate(context);
      validScopes = (Collection<TopicIF>) scopes.getValues(scopes
          .getLastIndex());
    }

    PathExpression type = (PathExpression) getType();
    ResultSet types = type.evaluate(context);
    Collection<TopicIF> validTypes = (Collection<TopicIF>) types
        .getValues(types.getLastIndex());

    LinkedList<AssociationIF> assocs = new LinkedList<AssociationIF>();
    for (Object t : validTypes) {
      Collection<AssociationIF> as = index.getAssociations((TopicIF) t);
      if (validScopes == null) {
        assocs.addAll(as);
      } else {
        for (AssociationIF assoc : as) {
          if (containsAny(assoc.getScope(), validScopes)) {
            assocs.add(assoc);
          }
        }
      }
    }

    TopicIF topic = null;
    if (input instanceof TopicIF) {
      topic = (TopicIF) input;
    }

    PathExpression leftRole = null, rightRole = null;
    switch (getChildCount()) {
    case 1:
      rightRole = (PathExpression) getChild(0);
      break;
    case 2:
      leftRole = (PathExpression) getChild(0);
      rightRole = (PathExpression) getChild(1);
      break;
    default:
      throw new InvalidQueryException(
          "Missing roles in association path element.");
    }

    LinkedList<AssociationRoleIF> finalRoles;
    if (leftRole != null) {
      finalRoles = getValidAssociationRoles(context, assocs, leftRole, topic);
    } else {
      finalRoles = new LinkedList<AssociationRoleIF>();
      for (AssociationIF assoc : assocs) {
        finalRoles.addAll(assoc.getRoles());
      }
    }

    Collection<TopicIF> validRoles = null;
    if (!rightRole.isEmpty()) {
      ResultSet roles = rightRole.evaluate(context);
      validRoles = (Collection<TopicIF>) roles.getValues(roles.getLastIndex());
    }
    
    LinkedList<Object[]> result = new LinkedList<Object[]>();
    for (AssociationRoleIF role : finalRoles) {
      if (validRoles == null || validRoles.contains(role.getType())) {
        result.add(new Object[] { role.getAssociation(), role.getPlayer() });
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private LinkedList<AssociationRoleIF> getValidAssociationRoles(
      LocalContext context, LinkedList<AssociationIF> assocs,
      PathExpression roleType, TopicIF input) throws InvalidQueryException {

    LinkedList<AssociationRoleIF> validRoles = new LinkedList<AssociationRoleIF>();
    if (input == null) {
      return validRoles;
    }
    
    Collection<TopicIF> validRoleTypes = null;
    if (!roleType.isEmpty()) {
      ResultSet roles = roleType.evaluate(context);
      validRoleTypes = (Collection<TopicIF>) roles.getValues(roles.getLastIndex());
    }
    
    for (AssociationIF assoc : assocs) {
      Collection<AssociationRoleIF> roles = assoc.getRoles();
      for (AssociationRoleIF role : roles) {
        if (validRoleTypes == null || validRoleTypes.contains(role.getType())) {
          if (role.getPlayer().getObjectId().equals(input.getObjectId())) {
            // add all the other roles of this assoc
            Collection<AssociationRoleIF> tmp = new HashSet<AssociationRoleIF>(roles);
            tmp.remove(role);
            validRoles.addAll(tmp);
          }
        }
      }
    }
    
    return validRoles;
  }
}
