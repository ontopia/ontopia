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

/**
 * INTERNAL: JDOQL boolean expression value: true or false.
 */

public class JDOValueExpression implements JDOExpressionIF {
  
  protected JDOValueIF value;

  public JDOValueExpression(JDOValueIF value) {
    // NOTE: value must have boolean value type
    this.value = value;
  }
  
  @Override
  public int getType() {
    return VALUE_EXPRESSION;
  }
  
  public JDOValueIF getValue() {
    return value;
  }

  public void setValue(JDOValueIF value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof JDOValueExpression) {
      JDOValueExpression other = (JDOValueExpression)obj;
      return value.equals(other.value);
    }
    return false;
  }

  @Override
  public String toString() {
    return "expr(" + value + ')';
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(value);
  }
  
}
