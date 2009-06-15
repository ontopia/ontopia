// $Id: JDONull.java,v 1.9 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL value: null. Class used to represent null. Syntax:
 * 'null'.
 */

public class JDONull implements JDOValueIF {

  public JDONull() {
  }

  public int getType() {
    return NULL;
  }

  public int hashCode() {
    return 321; // Just some random number
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDONull)
      return true;
    else
      return false;
  }

  public String toString() {
    return "null";
  }

  public void visit(JDOVisitorIF visitor) {
  }
  
}






