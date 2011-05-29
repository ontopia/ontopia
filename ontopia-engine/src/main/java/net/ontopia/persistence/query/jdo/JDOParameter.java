
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL value: parameter. Class used to reference
 * parameters by name. Syntax: 'PARAMETER'.
 */

public class JDOParameter implements JDOValueIF {

  protected String name;

  public JDOParameter(String name) {
    if (name == null) throw new NullPointerException("Parameter name cannot be null.");
    this.name = name;
  }
  
  public int getType() {
    return PARAMETER;
  }
  
  public String getName() {
    return name;
  }

  public int hashCode() {
    return name.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOParameter) {
      JDOParameter other = (JDOParameter)obj;
      return name.equals(other.name);
    }
    return false;  
  }

  public String toString() {
    return "?" + name;
  }

  public void visit(JDOVisitorIF visitor) {
  }
  
}
