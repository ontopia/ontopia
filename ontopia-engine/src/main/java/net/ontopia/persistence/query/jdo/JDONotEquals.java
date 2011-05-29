
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL method: !Object.equals(Object) (!=). Syntax: 'A != B'.
 */

public class JDONotEquals implements JDOExpressionIF {

  protected JDOValueIF left;
  protected JDOValueIF right;

  public JDONotEquals(JDOValueIF left, JDOValueIF right) {
    this.left = left;
    this.right = right;
  }
  
  public int getType() {
    return NOT_EQUALS;
  }
  
  public JDOValueIF getLeft() {
    return left;
  }
  
  public JDOValueIF getRight() {
    return right;
  }
  
  public int hashCode() {
    return left.hashCode() + right.hashCode() + NOT_EQUALS;
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDONotEquals) {
      JDONotEquals other = (JDONotEquals)obj;
      return (left.equals(other.left) &&
              right.equals(other.right));
    }
    return false;
  }

  public String toString() {
    return left + " != " + right;
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(left);
    visitor.visitable(right);
  }
  
}






