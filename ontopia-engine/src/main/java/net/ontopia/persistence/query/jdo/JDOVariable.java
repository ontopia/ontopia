// $Id: JDOVariable.java,v 1.11 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL value: variable. Class used to reference variables
 * by name. Syntax: 'VARIABLE'.
 */

public class JDOVariable implements JDOValueIF {

  protected String name;

  public JDOVariable(String name) {
    if (name == null) throw new NullPointerException("Variable name cannot be null.");
    this.name = name;
  }
  
  public int getType() {
    return VARIABLE;
  }
  
  public String getName() {
    return name;
  }
  
  public int hashCode() {
    return name.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOVariable){
      JDOVariable other = (JDOVariable)obj;
      return name.equals(other.name);
    }
    return false;
  }

  public String toString() {
    return name;
  }

  public void visit(JDOVisitorIF visitor) {
  }
  
}






