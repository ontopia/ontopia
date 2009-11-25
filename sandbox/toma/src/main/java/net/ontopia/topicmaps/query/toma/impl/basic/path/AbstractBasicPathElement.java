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

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathElement;
import net.ontopia.topicmaps.query.toma.parser.ast.PathExpressionIF;

public abstract class AbstractBasicPathElement extends AbstractPathElement
    implements BasicPathElementIF {
  
  protected String[] columns;
  protected int resultSize;
  
  protected boolean assignScope;
  protected boolean assignType;
  
  protected AbstractBasicPathElement(String name) {
    super(name);
  }

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
    if (getBoundVariable() != null) {
      resultSize++;
    }
    
    columns = new String[resultSize];

    int idx = 0;
    if (getBoundInputVariable() != null) {
      columns[idx++] = getBoundInputVariable().toString();
    }
    if (containsSoleUnboundVariable(getScope(), context)) {
      columns[idx++] = getVariableName(getScope());
    }
    if (containsSoleUnboundVariable(getType(), context)) {
      columns[idx++] = getVariableName(getType());
    }
    if (getBoundVariable() != null) {
      columns[idx++] = getBoundVariable().toString();
    }
  }
  
  public final String[] getColumnNames() {
    return columns;
  }

  public final int getResultSize() {
    return resultSize;
  }
  
  protected boolean isAssignScope() {
    return assignScope;
  }
  
  protected boolean isAssignType() {
    return assignType;
  }
  
  protected int getResultArraySize() {
    int size = 1;
    if (getBoundInputVariable() != null) {
      size++;
    }
    if (isAssignScope()) {
      size++;
    }
    if (isAssignType()) {
      size++;
    }
    return size;
  }
  
  protected boolean containsSoleUnboundVariable(PathExpressionIF expr,
      LocalContext context) {
    if (expr != null) {
      PathExpression e = (PathExpression) expr;
      String varName = e.getVariableName();
      if (varName != null && e.getPathLength() == 1) {
        if (context.getResultSet(varName) == null) {
          return true;
        }
      }
    }
    return false;
  }

  protected String getVariableName(PathExpressionIF expr) {
    if (expr != null) {
      PathExpression e = (PathExpression) expr;
      return e.getVariableName();
    }
    return null;
  }
  
  /**
   * Checks whether collection a contains at least one item from collection b.
   * 
   * @return true if collection a contains any item from collection b, false
   *         otherwise.
   */
  protected boolean containsAny(Collection<?> a, Collection<?> b) {
    for (Object obj : b) {
      if (a.contains(obj)) {
        return true;
      }
    }
    return false;
  }
}
