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
 * INTERNAL: JDOQL value: string. Class used to represent
 * java.lang.String instances. Syntax: '"hello"'.
 */

public class JDOString implements JDOValueIF {

  protected String value;
  
  public JDOString(String value) {
    if (value == null) {
      throw new IllegalArgumentException("String value cannot be null.");
    }
    
    this.value = value;
  }

  @Override
  public int getType() {
    return STRING;
  }

  public String getValue() {
    return value;
  }
  
  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JDOString) {
      JDOString other = (JDOString)obj;    
      return value.equals(other.value);
    }
    return false;
  }

  @Override
  public String toString() {
    return "\"" + value + "\"";
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    // no-op
  }
  
}
