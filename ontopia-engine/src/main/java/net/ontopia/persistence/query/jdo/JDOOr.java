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

package net.ontopia.persistence.query.jdo;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * INTERNAL: JDOQL logical expression: or (||). Syntax: '( ... || ... )'.
 */

public class JDOOr implements JDOExpressionIF {

  protected JDOExpressionIF[] expressions;

  public JDOOr(List expressions) {
    this((JDOExpressionIF[])expressions.toArray(new JDOExpressionIF[expressions.size()]));
  }

  public JDOOr(JDOExpressionIF[] expressions) {
    setExpressions(expressions);
  }

  public JDOOr(JDOExpressionIF expr) {
    this(new JDOExpressionIF[] { expr });
  }
  
  public JDOOr(JDOExpressionIF expr1,
               JDOExpressionIF expr2) {
    this(new JDOExpressionIF[] { expr1, expr2 });
  }
  
  public JDOOr(JDOExpressionIF expr1,
               JDOExpressionIF expr2,
               JDOExpressionIF expr3) {
    this(new JDOExpressionIF[] { expr1, expr2, expr3 });
  }
  
  public JDOOr(JDOExpressionIF expr1,
               JDOExpressionIF expr2,
               JDOExpressionIF expr3,
               JDOExpressionIF expr4) {
    this(new JDOExpressionIF[] { expr1, expr2, expr3, expr4 });
  }
  
  public JDOOr(JDOExpressionIF expr1,
               JDOExpressionIF expr2,
               JDOExpressionIF expr3,
               JDOExpressionIF expr4,
               JDOExpressionIF expr5) {
    this(new JDOExpressionIF[] { expr1, expr2, expr3, expr4, expr5 });
  }

  public void setExpressions(JDOExpressionIF[] expressions) {
    Objects.requireNonNull(expressions, "Or expression must have nested expression.");
    if (expressions.length == 0) { throw new IllegalArgumentException("Or expression must have nested expressions."); }
    this.expressions = expressions;
  }
  
  @Override
  public int getType() {
    return OR;
  }
  
  public JDOExpressionIF[] getExpressions() {
    return expressions;
  }

  @Override
  public int hashCode() {
    int hashCode = 0;
    for (int ix = 0; ix < expressions.length; ix++) {
      if (expressions[ix] != null) {
        hashCode = (hashCode + expressions[ix].hashCode()) & 0x7FFFFFFF;
      }
    }
    return hashCode;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof JDOOr) {
      JDOOr other = (JDOOr)obj;
      if (Arrays.equals(expressions, other.getExpressions())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    for (int i=0; i < expressions.length; i++) {
      if (i != 0) {
        sb.append(" || ");
      }
      sb.append(expressions[i]);
    }
    sb.append(')');
    return sb.toString();
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(expressions);
  }
  
}






