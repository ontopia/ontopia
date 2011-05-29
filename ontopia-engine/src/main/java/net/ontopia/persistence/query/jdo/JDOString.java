
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL value: string. Class used to represent
 * java.lang.String instances. Syntax: '"hello"'.
 */

public class JDOString implements JDOValueIF {

  protected String value;
  
  public JDOString(String value) {
    if (value == null)
      throw new IllegalArgumentException("String value cannot be null.");
    
    this.value = value;
  }

  public int getType() {
    return STRING;
  }

  public String getValue() {
    return value;
  }
  
  public int hashCode() {
    return value.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOString) {
      JDOString other = (JDOString)obj;    
      return value.equals(other.value);
    }
    return false;
  }

  public String toString() {
    return "\"" + value + "\"";
  }

  public void visit(JDOVisitorIF visitor) {
  }
  
}
