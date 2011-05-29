
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL method: Collection.isEmpty(). Syntax: 'A.isEmpty()'.
 */

public class JDOIsEmpty implements JDOExpressionIF {

  protected JDOValueIF value;

  public JDOIsEmpty(JDOField value) {
    this((JDOValueIF)value);
  }
  
  public JDOIsEmpty(JDOValueIF value) {
    // FIXME: Can only be field[collection] or variable[collection]
    this.value = value;
  }
  
  public int getType() {
    return IS_EMPTY;
  }
  
  public JDOValueIF getValue() {
    return value;
  }
  
  public int hashCode() {
    return value.hashCode() + IS_EMPTY;
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOIsEmpty) {
      JDOIsEmpty other = (JDOIsEmpty)obj;
      return (value.equals(other.value));
    }
    return false;
  }

  public String toString() {
    return value + ".isEmpty()";
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(value);
  }
  
}






