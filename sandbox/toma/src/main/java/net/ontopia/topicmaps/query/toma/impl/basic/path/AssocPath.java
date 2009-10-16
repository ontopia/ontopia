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
  public Collection<Object[]> evaluate(LocalContext context, Object input)
      throws InvalidQueryException {

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

    TopicIF topic = null;
    if (input instanceof TopicIF) {
      topic = (TopicIF) input;
    }

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

    if (leftRole != null) {
      return evaluateLeft(context, topic, leftRole, rightRole, validTypes,
          validScopes);
    } else {
      ClassInstanceIndexIF index = (ClassInstanceIndexIF) context.getTopicMap()
          .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

      Collection<AssociationRoleIF> roles = new LinkedList<AssociationRoleIF>();
      for (TopicIF t : validTypes) {
        Collection<AssociationIF> assocs = index.getAssociations((TopicIF) t);
        for (AssociationIF a : assocs) {
          if (validScopes == null || containsAny(a.getScope(), validScopes)) {
            roles.addAll(a.getRoles());
          }
        }
      }

      return evaluateRight(context, rightRole, roles);
    }

  }

  @SuppressWarnings("unchecked")
  private Collection<Object[]> evaluateLeft(LocalContext context,
      TopicIF input, PathExpression left, PathExpression right,
      Collection<TopicIF> validTypes, Collection<TopicIF> validScopes)
      throws InvalidQueryException {
    Collection<AssociationRoleIF> inputRoles = input.getRoles();
    Collection<AssociationRoleIF> result = new LinkedList<AssociationRoleIF>();

    Collection<TopicIF> validRoleTypes = null;
    if (!left.isEmpty()) {
      ResultSet roles = left.evaluate(context);
      validRoleTypes = (Collection<TopicIF>) roles.getValues(roles
          .getLastIndex());
    }

    for (AssociationRoleIF role : inputRoles) {
      if (validRoleTypes == null || validRoleTypes.contains(role.getType())) {
        AssociationIF a = role.getAssociation();
        if (validTypes == null || validTypes.contains(a.getType())) {
          if (validScopes == null || containsAny(a.getScope(), validScopes)) {
            // add all the other roles of this assoc
            Collection<AssociationRoleIF> tmp = new HashSet<AssociationRoleIF>(
                a.getRoles());
            tmp.remove(role);
            result.addAll(tmp);
          }
        }
      }
    }

    return evaluateRight(context, right, result);
  }

  @SuppressWarnings("unchecked")
  private Collection<Object[]> evaluateRight(LocalContext context,
      PathExpression right, Collection<AssociationRoleIF> roles)
      throws InvalidQueryException {

    Collection<TopicIF> validRoles = null;
    if (!right.isEmpty()) {
      ResultSet rs = right.evaluate(context);
      validRoles = (Collection<TopicIF>) rs.getValues(rs.getLastIndex());
    }

    Collection<Object[]> result = new LinkedList<Object[]>();
    for (AssociationRoleIF role : roles) {
      if (validRoles == null || validRoles.contains(role.getType())) {
        result.add(new Object[] { role.getAssociation(), role.getPlayer() });
      }
    }
    return result;
  }
}
