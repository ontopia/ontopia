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

  private Collection<TopicIF> validScopes = null;
  private Collection<TopicIF> validTypes = null;
  private Collection<TopicIF> validLeftRoles = null;
  private Collection<TopicIF> validRightRoles = null;
  
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

    if (getScope() != null) {
      PathExpression scope = (PathExpression) getScope();
      // Optimization: if the scope expression does not contain a variable, we
      // can cache it.
      if (scope.getVariableName() != null || validScopes == null) {
        ResultSet scopes = scope.evaluate(context);
        validScopes = (Collection<TopicIF>) scopes.getValidValues(scopes
            .getLastIndex());
      }
    }

    PathExpression type = (PathExpression) getType();
    // Optimization: if the type expression does not contain a variable, we
    // can cache it.
    if (type.getVariableName() != null || validTypes == null) {
      ResultSet types = type.evaluate(context);
      validTypes = (Collection<TopicIF>) types.getValidValues(types.getLastIndex());
    }

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

    // Optimization: if the type expression does not contain a variable, we
    // can cache it.
    if (!left.isEmpty()
        && (left.getVariableName() != null || validRightRoles == null)) {
      ResultSet roles = left.evaluate(context);
      validLeftRoles = (Collection<TopicIF>) roles.getValidValues(roles
          .getLastIndex());
    }

    for (AssociationRoleIF role : inputRoles) {
      if (validLeftRoles == null || validLeftRoles.contains(role.getType())) {
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

    // Optimization: if the type expression does not contain a variable, we
    // can cache it.
    if (!right.isEmpty()
        && (right.getVariableName() != null || validRightRoles == null)) {
      ResultSet rs = right.evaluate(context);
      validRightRoles = (Collection<TopicIF>) rs.getValidValues(rs.getLastIndex());
    }

    Collection<Object[]> result = new LinkedList<Object[]>();
    boolean includeAssoc = getBoundInputVariable() != null;
    for (AssociationRoleIF role : roles) {
      if (validRightRoles == null || validRightRoles.contains(role.getType())) {
        if (includeAssoc) {
          result.add(new Object[] { role.getAssociation(), role.getPlayer() });
        } else {
          result.add(new Object[] { role.getPlayer() });
        }
      }
    }
    return result;
  }
}
