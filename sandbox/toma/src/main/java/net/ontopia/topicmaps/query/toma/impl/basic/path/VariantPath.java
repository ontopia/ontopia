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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;

/**
 * INTERNAL: Variant path element in an path expression. Returns all variants
 * of a given input name.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>NAME
 * </ul>
 * </p><p>
 * <b>Output</b>: VARIANT
 * </p>
 */
public class VariantPath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.NAME);
  }
  
  private Collection<?> validScopes = null;

  public VariantPath() {
    super("VAR");
  }

  protected boolean isLevelAllowed() {
    return false;
  }

  protected boolean isScopeAllowed() {
    return true;
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
    return TYPE.VARIANT;
  }
  
  @SuppressWarnings("unchecked")
  public Collection evaluate(LocalContext context, Object input)
      throws InvalidQueryException {
    TopicNameIF name = (TopicNameIF) input;
    
    if (getScope() != null && !isAssignScope()) {
      PathExpression scope = (PathExpression) getScope();
      // Optimization: if the scope expression does not contain a variable, we
      // can cache it.
      if (scope.getVariableName() != null || validScopes == null) {
        ResultSet scopes = scope.evaluate(context);
        validScopes = scopes.getValidValues(scopes.getLastIndex());
      }
    }

    Collection<VariantNameIF> variants = name.getVariants();
    if (validScopes == null && !isAssignScope()) {
      return variants;
    } else {
      Collection<Object[]> result = new LinkedList<Object[]>();
      for (VariantNameIF var : variants) {
        if (validScopes == null || containsAny(var.getScope(), validScopes)) {
          fillResultCollection(result, var);
        }
      }
      return result;
    }
  }
  
  @SuppressWarnings("unchecked")
  private void fillResultCollection(Collection<Object[]> result,
      VariantNameIF var) {
    if (isAssignScope()) {
      Collection<TopicIF> scopes = var.getScope();
      if (scopes.isEmpty()) {
        result.add(new Object[] { null, var });
      } else {
        for (TopicIF scope : scopes) {
          result.add(new Object[] { scope, var });
        }
      }
    } else {
      result.add(new Object[] { var });
    }
  }
}
