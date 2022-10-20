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
 * INTERNAL: JDOQL value: primitive. Class used to reference primitive
 * values. These can be boolean, byte, short, integer, long, float or
 * double. [FIXME: In addition the classes BigInteger and BigDouble
 * are supported.] Syntax: the same as their Java syntax
 * representations.
 */

public class JDOPrimitive implements JDOValueIF {

  // boolean
  
  public static final int BOOLEAN = 1;
  public static final int BYTE = 2;

  // numeric
  
  public static final int SHORT = 3;
  public static final int INTEGER = 4;
  public static final int LONG = 5;
  public static final int FLOAT = 6;
  public static final int DOUBLE = 7;
  
  public static final int BIGDECIMAL = 8;
  public static final int BIGINTEGER = 9;
  
  protected int value_type;
  protected Object value;

  public JDOPrimitive(int value_type, Object value) {
    if (value == null) {
      throw new IllegalArgumentException("Primitive value cannot be null (value type: " + value_type  + ").");
    }

    // Validate value type
    switch (value_type) {
    case BOOLEAN:
    case BYTE:
    case SHORT:
    case INTEGER:
    case LONG:
    case FLOAT:
    case DOUBLE:
    case BIGDECIMAL:
    case BIGINTEGER:
      break;
    default:
      throw new IllegalArgumentException("Invalid value type: " + value_type  + ".");
    }
    
    this.value_type = value_type;
    this.value = value;
  }

  @Override
  public int getType() {
    return PRIMITIVE;
  }

  public Class getValueType() {
    return value.getClass();
  }
  
  /**
   * INTERNAL: Returns the type of primitive as indicated by one of
   * the constants in the {@link JDOPrimitive} interface.
   */
  public int getPrimitiveType() {
    return value_type;
  }

  /**
   * INTERNAL: Returns the primitive value. An object wrapper is
   * represented using its primitive wrapper class.
   */
  public Object getValue() {
    return value;
  }

  /**
   * INTERNAL: Returns true if the primitive is of numeric type.
   */
  public boolean isNumeric() {
    switch (getPrimitiveType()) {
    case BOOLEAN:
    case BYTE:
      return false;
    default:
      return true;
    }
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
    if (obj instanceof JDOPrimitive) {
      JDOPrimitive other = (JDOPrimitive)obj;    
      return (value.equals(other.value) && value_type == other.value_type);
    }
    return false;
  }
  
  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    // no-op
  }
  
}






