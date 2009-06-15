// $Id: JDOPrimitive.java,v 1.14 2006/02/06 06:56:08 grove Exp $

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
    if (value == null)
      throw new IllegalArgumentException("Primitive value cannot be null (value type: " + value_type  + ").");

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
  
  public int hashCode() {
    return value.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOPrimitive) {
      JDOPrimitive other = (JDOPrimitive)obj;    
      return (value.equals(other.value) && value_type == other.value_type);
    }
    return false;
  }
  
  public String toString() {
    return value.toString();
  }

  public void visit(JDOVisitorIF visitor) {
  }
  
}






