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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;

/**
 * INTERNAL: Occurrence path element in an path expression. Returns all
 * occurrences of a given input topic.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * </ul>
 * </p>
 * <p>
 * <b>Output</b>: OCCURRENCE
 * </p>
 */
public class OccurrencePath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;

  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }

  private Collection<TopicIF> validScopes = null;
  private Collection<TopicIF> validTypes = null;

  public OccurrencePath() {
    super("OC");
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
    return false;
  }

  public Set<TYPE> validInput() {
    return inputSet;
  }

  public TYPE output() {
    return TYPE.OCCURRENCE;
  }

  @SuppressWarnings("unchecked")
  public Collection evaluate(LocalContext context, Object input)
      throws InvalidQueryException {
    TopicIF topic = (TopicIF) input;

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

    if (getType() != null && !isAssignType()) {
      PathExpression type = (PathExpression) getType();
      // Optimization: if the type expression does not contain a variable, we
      // can cache it.
      if (type.getVariableName() != null || validTypes == null) {
        ResultSet types = type.evaluate(context);
        validTypes = (Collection<TopicIF>) types.getValidValues(types
            .getLastIndex());
      }
    }

    Collection<OccurrenceIF> ocs = topic.getOccurrences();
    if (validTypes == null && validScopes == null && !isAssignScope()
        && !isAssignType()) {
      return ocs;
    } else {
      Collection<Object[]> result = new LinkedList<Object[]>();
      for (OccurrenceIF oc : ocs) {
        TopicIF ocType = oc.getType();
        if (validTypes == null || validTypes.contains(ocType)) {
          if (validScopes == null || containsAny(oc.getScope(), validScopes)) {
            fillResultCollection(result, oc);
          }
        }
      }
      return result;
    }
  }
  
  @SuppressWarnings("unchecked")
  private void fillResultCollection(Collection<Object[]> result,
      OccurrenceIF oc) {
    if (isAssignScope()) {
      Collection<TopicIF> scopes = oc.getScope();
      if (scopes.isEmpty()) {
        if (isAssignType()) {
          result.add(new Object[] { null, oc.getType(), oc });
        } else {
          result.add(new Object[] { null, oc });
        }
      } else {
        for (TopicIF scope : scopes) {
          result.add(new Object[] { scope, oc.getType(), oc });
        }
      }
    } else if (isAssignType()) {
      result.add(new Object[] { oc.getType(), oc });
    } else {
      result.add(new Object[] { oc });
    }
  }
}
