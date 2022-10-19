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
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: SQL logical expression: and
 */

public class SQLAnd implements SQLExpressionIF {

  protected SQLExpressionIF[] expressions;

  public SQLAnd(SQLExpressionIF expr1, SQLExpressionIF expr2) {
    this.expressions = new SQLExpressionIF[] {expr1, expr2};
  }

  public SQLAnd(SQLExpressionIF expr1, SQLExpressionIF expr2, SQLExpressionIF expr3) {
    this.expressions = new SQLExpressionIF[] {expr1, expr2, expr3};
  }
  
  public SQLAnd(SQLExpressionIF[] expressions) {
    Objects.requireNonNull(expressions, "And expressions cannot be null.");
    this.expressions = expressions;
  }

  @Override
  public int getType() {
    return AND;
  }

  public SQLExpressionIF[] getExpressions() {
    return expressions;
  }

  public void setExpressions(SQLExpressionIF[] expressions) {
    this.expressions = expressions;
  }

  @Override
  public String toString() {
    return "(" + StringUtils.join(expressions, " and ") + ")";
  }
  
}
