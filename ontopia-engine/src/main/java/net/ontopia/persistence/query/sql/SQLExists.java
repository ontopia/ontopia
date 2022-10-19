/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.persistence.query.sql;

import java.util.Objects;

/**
 * INTERNAL: SQL condition: exists. Evaluates to true if the left
 * value contains the right value.<p>
 *
 * An EXISTS condition tests for existence of rows in a subquery.<p>
 */

public class SQLExists implements SQLExpressionIF {

  protected SQLExpressionIF expression;

  public SQLExists(SQLExpressionIF expression) {
    Objects.requireNonNull(expression, "Expressions must not be null.");
    this.expression = expression;
  }

  @Override
  public int getType() {
    return EXISTS;
  }

  public SQLExpressionIF getExpression() {
    return expression;
  }

  public void setExpression(SQLExpressionIF expression) {
    this.expression = expression;
  }

  @Override
  public String toString() {
    return "exists (" + getExpression() + ")";
  }
  
}





