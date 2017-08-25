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

public class JDOBoolean implements JDOExpressionIF {

  public static final JDOBoolean TRUE = new JDOBoolean(true);
  public static final JDOBoolean FALSE = new JDOBoolean(false);
  
  protected boolean value;

  public JDOBoolean(boolean value) {
    this.value = value;
  }
  
  public int getType() {
    return BOOLEAN;
  }
  
  public boolean getValue() {
    return value;
  }

  public int hashCode() {
    return value ? 1231 : 1237;
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof JDOBoolean) {
      JDOBoolean other = (JDOBoolean)obj;
      return value == other.value;
    }
    return false;
  }

  public String toString() {
    if (value)
      return "true";
    else
      return "false";
  }

  public void visit(JDOVisitorIF visitor) {
    // no-op
  }
  
}
