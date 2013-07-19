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

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: JDOQL aggregate function.
 */

public class JDOAggregate implements JDOAggregateIF {

  protected int type;
  protected JDOValueIF value;

  public JDOAggregate(JDOValueIF value, int type) {
    if (value == null)
      throw new NullPointerException("Aggregate function variable cannot not be null.");
    this.value = value;
    this.type = type;
  }
  
  public int getType() {
    return type;
  }
  
  public JDOValueIF getValue() {
    return value;
  }

  public int hashCode() {
    return value.hashCode() + type;
  }

  public boolean equals(Object obj) {
    if (obj instanceof JDOAggregateIF) {
      JDOAggregateIF other = (JDOAggregateIF)obj;    
      return (type == other.getType() ||
              value.equals(other.getValue()));
    }
    return false;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    switch (type) {
    case COUNT:
      sb.append("count");
      break;
    default:
      throw new OntopiaRuntimeException("Unknown aggregate function type: " + type);
    }
    sb.append("(");
    sb.append(value);
    sb.append(")");
    return sb.toString();
  }
  
}






