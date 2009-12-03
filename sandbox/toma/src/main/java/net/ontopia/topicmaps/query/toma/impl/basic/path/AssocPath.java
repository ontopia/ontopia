/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;
import net.ontopia.utils.CompactHashSet;

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
@SuppressWarnings("unchecked")
public class AssocPath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;

  static {
    inputSet = new CompactHashSet();
    inputSet.add(TYPE.TOPIC);
    inputSet.add(TYPE.NONE);
  }

  private Collection<TopicIF> validScopes = null;
  private Collection<TopicIF> validTypes = null;
  private Collection<TopicIF> validLeftRoles = null;
  private Collection<TopicIF> validRightRoles = null;

  private PathExpression leftRole = null;
  private PathExpression rightRole = null;
  
  private boolean leftTypeAssign;
  private boolean rightTypeAssign;
  
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

  protected boolean isAssignLeftType() {
    return leftTypeAssign;
  }
  
  protected boolean isAssignRightType() {
    return rightTypeAssign;
  }
  
  public Set<TYPE> validInput() {
    return inputSet;
  }

  public TYPE output() {
    return TYPE.TOPIC;
  }

  @Override
  public void initResultSet(LocalContext context) {
    resultSize = 0;
    if (getBoundInputVariable() != null) {
      resultSize++;
    }
    if (containsSoleUnboundVariable(getScope(), context)) {
      assignScope = true;
      resultSize++;
    }
    if (containsSoleUnboundVariable(getType(), context)) {
      assignType = true;
      resultSize++;
    }
    
    switch (getChildCount()) {
    case 1:
      rightRole = (PathExpression) getChild(0);
      break;
    case 2:
      leftRole = (PathExpression) getChild(0);
      rightRole = (PathExpression) getChild(1);
      break;
    }
    
    if (leftRole != null && containsSoleUnboundVariable(leftRole, context)) {
      leftTypeAssign = true;
      resultSize++;
    }

    if (rightRole != null && containsSoleUnboundVariable(rightRole, context)) {
      rightTypeAssign = true;
      resultSize++;
    }
    
    if (getBoundVariable() != null) {
      resultSize++;
    }
    
    columns = new String[resultSize];

    int idx = 0;
    if (getBoundInputVariable() != null) {
      columns[idx++] = getBoundInputVariable().toString();
    }
    if (assignScope) {
      columns[idx++] = getVariableName(getScope());
    }
    if (assignType) {
      columns[idx++] = getVariableName(getType());
    }
    if (leftTypeAssign) {
      columns[idx++] = getVariableName(leftRole);
    }
    if (rightTypeAssign) {
      columns[idx++] = getVariableName(rightRole);
    }
    if (getBoundVariable() != null) {
      columns[idx++] = getBoundVariable().toString();
    }
  }
  
  public Collection<Object[]> evaluate(LocalContext context, Object input)
      throws InvalidQueryException {
    TopicIF topic = null;
    if (input instanceof TopicIF) {
      topic = (TopicIF) input;
    }

    if (getScope() != null && !isAssignScope()) {
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
    if (!isAssignType()) {
      // Optimization: if the type expression does not contain a variable, we
      // can cache it.
      if (type.getVariableName() != null || validTypes == null) {
        ResultSet types = type.evaluate(context);
        validTypes = (Collection<TopicIF>) types.getValidValues(types
            .getLastIndex());
      }
    }

    if (leftRole != null) {
      return evaluateLeft(context, topic, leftRole, rightRole, validTypes,
          validScopes);
    } else {
      ClassInstanceIndexIF index = (ClassInstanceIndexIF) context.getTopicMap()
          .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

      Collection<TopicIF> assocTypes = validTypes;
      if (assocTypes == null) {
        assocTypes = index.getAssociationTypes();
      }
      
      Collection<Object[]> roles = new ArrayList<Object[]>();
      for (TopicIF t : assocTypes) {
        Collection<AssociationIF> assocs = index.getAssociations((TopicIF) t);
        for (AssociationIF a : assocs) {
          if (validScopes == null || containsAny(a.getScope(), validScopes)) {
            for (Object role : a.getRoles()) {
              roles.add(new Object[] { role });
            }
          }
        }
      }

      return evaluateRight(context, rightRole, roles);
    }

  }

  private Collection<Object[]> evaluateLeft(LocalContext context,
      TopicIF input, PathExpression left, PathExpression right,
      Collection<TopicIF> validTypes, Collection<TopicIF> validScopes)
      throws InvalidQueryException {
    if (!isAssignLeftType()) {
      // Optimization: if the type expression does not contain a variable, we
      // can cache it.
      if (!left.isEmpty()
          && (left.getVariableName() != null || validRightRoles == null)) {
        ResultSet roles = left.evaluate(context);
        validLeftRoles = (Collection<TopicIF>) roles.getValidValues(roles
            .getLastIndex());
      }
    }

    Collection<AssociationRoleIF> inputRoles = input.getRoles();
    Collection<Object[]> result = new ArrayList<Object[]>(inputRoles.size());
    for (AssociationRoleIF role : inputRoles) {
      if (validLeftRoles == null || validLeftRoles.contains(role.getType())) {
        AssociationIF a = role.getAssociation();
        if (validTypes == null || validTypes.contains(a.getType())) {
          if (validScopes == null || containsAny(a.getScope(), validScopes)) {
            // add all the other roles of this assoc
            Set<AssociationRoleIF> tmp = new CompactHashSet(a.getRoles());
            tmp.remove(role);
            
            for (AssociationRoleIF otherRole : tmp) {
              result.add(new Object[] { role.getType(), otherRole });
            }
          }
        }
      }
    }

    return evaluateRight(context, right, result);
  }

  private Collection<Object[]> evaluateRight(LocalContext context,
      PathExpression right, Collection<Object[]> roles)
      throws InvalidQueryException {

    if (!isAssignRightType()) {
      // Optimization: if the type expression does not contain a variable, we
      // can cache it.
      if (!right.isEmpty()
          && (right.getVariableName() != null || validRightRoles == null)) {
        ResultSet rs = right.evaluate(context);
        validRightRoles = (Collection<TopicIF>) rs.getValidValues(rs
            .getLastIndex());
      }
    }

    Collection<Object[]> result = new ArrayList<Object[]>(roles.size());
    for (Object[] row : roles) {
      AssociationRoleIF role = (AssociationRoleIF) row[row.length - 1];
      TopicIF leftRoleType = null;
      if (row.length == 2) {
        leftRoleType = (TopicIF) row[0];
      }
      if (validRightRoles == null || validRightRoles.contains(role.getType())) {
        fillResultCollection(result, role, leftRoleType);
      }
    }
    return result;
  }
  
  private void fillResultCollection(Collection<Object[]> result,
      AssociationRoleIF role, TopicIF leftRoleType) {
    
    if (isAssignScope()) {
      Collection<TopicIF> scopes = role.getAssociation().getScope();
      if (scopes != null && !scopes.isEmpty()) {
        for (TopicIF scope : scopes) {
          fillSingleResult(result, role, scope, leftRoleType);
        }
      } else {
        fillSingleResult(result, role, null, leftRoleType);
      }
    } else {
      fillSingleResult(result, role, null, leftRoleType);
    }
  }
  
  private void fillSingleResult(Collection<Object[]> result,
      AssociationRoleIF role, TopicIF scope, TopicIF leftRoleType) {
    int rsSize = getResultSize();
    if (getBoundVariable() == null) {
      rsSize++;
    }
    
    Object[] row = new Object[rsSize];
    int idx = 0;
    
    if (getBoundInputVariable() != null) {
      row[idx++] = role.getAssociation();
    }
    
    if (isAssignScope()) {
      row[idx++] = scope;
    }

    if (isAssignType()) {
      row[idx++] = role.getAssociation().getType();
    }

    if (isAssignLeftType()) {
      row[idx++] = leftRoleType;
    }
      
    if (isAssignRightType()) {
      row[idx++] = role.getType();
    }
    
    row[idx] = role.getPlayer();
    result.add(row);
  }
}
