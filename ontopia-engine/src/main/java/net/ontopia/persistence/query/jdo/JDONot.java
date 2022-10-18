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

import java.util.Objects;

/**
 * INTERNAL: JDOQL logical expression: not (!). Syntax: '!( ... )'.
 */

public class JDONot implements JDOExpressionIF {

  protected JDOExpressionIF expression;

  public JDONot(JDOExpressionIF expression) {
    Objects.requireNonNull(expression, "Not expression must have nested expression.");
    this.expression = expression;
  }
  
  @Override
  public int getType() {
    return NOT;
  }
  
  public JDOExpressionIF getExpression() {
    return expression;
  }

  @Override
  public int hashCode() {
    return expression.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof JDONot) {
      JDONot other = (JDONot)obj;
      return expression.equals(other.getExpression());
    }
    return false;
  }

  @Override
  public String toString() {
    return new StringBuilder("!(")
        .append(expression)
        .append(')')
        .toString();
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(expression);
  }
  
}






