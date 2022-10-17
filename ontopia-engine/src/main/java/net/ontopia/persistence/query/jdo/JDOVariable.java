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
 * INTERNAL: JDOQL value: variable. Class used to reference variables
 * by name. Syntax: 'VARIABLE'.
 */

public class JDOVariable implements JDOValueIF {

  protected String name;

  public JDOVariable(String name) {
    Objects.requireNonNull(name, "Variable name cannot be null.");
    this.name = name;
  }
  
  @Override
  public int getType() {
    return VARIABLE;
  }
  
  public String getName() {
    return name;
  }
  
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOVariable){
      JDOVariable other = (JDOVariable)obj;
      return name.equals(other.name);
    }
    return false;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    // no-op
  }
  
}






