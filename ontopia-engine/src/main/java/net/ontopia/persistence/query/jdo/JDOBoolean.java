
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
  }
  
}
