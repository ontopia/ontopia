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

import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: SQL logical expression: or
 */

public class SQLOr implements SQLExpressionIF {

  protected SQLExpressionIF[] expressions;

  public SQLOr(SQLExpressionIF[] expressions) {
    this.expressions = expressions;
  }

  public SQLOr(SQLExpressionIF expr1, SQLExpressionIF expr2) {
    this.expressions = new SQLExpressionIF[] {expr1, expr2};
  }

  public SQLOr(SQLExpressionIF expr1, SQLExpressionIF expr2, SQLExpressionIF expr3) {
    this.expressions = new SQLExpressionIF[] {expr1, expr2, expr3};
  }

  @Override
  public int getType() {
    return OR;
  }

  public SQLExpressionIF[] getExpressions() {
    return expressions;
  }

  public void setExpressions(SQLExpressionIF[] expressions) {
    this.expressions = expressions;
  }

  @Override
  public String toString() {
    return "(" + StringUtils.join(getExpressions(), " or ") + ")";
  }
  
}





