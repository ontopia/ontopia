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
 * INTERNAL: JDOQL value: null. Class used to represent null. Syntax:
 * 'null'.
 */

public class JDONull implements JDOValueIF {

  @Override
  public int getType() {
    return NULL;
  }

  @Override
  public int hashCode() {
    return 321; // Just some random number
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JDONull) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return "null";
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    // no-op
  }
  
}






