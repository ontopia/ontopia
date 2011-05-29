
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL method: Object.equals(Object) (==). Syntax: 'A == B'.
 */

public class JDOEquals implements JDOExpressionIF {

  protected JDOValueIF left;
  protected JDOValueIF right;

  public JDOEquals(JDOValueIF left, JDOValueIF right) {
    this.left = left;
    this.right = right;
  }
  
  public int getType() {
    return EQUALS;
  }
  
  public JDOValueIF getLeft() {
    return left;
  }
  
  public JDOValueIF getRight() {
    return right;
  }
  
  public int hashCode() {
    return left.hashCode() + right.hashCode() + EQUALS;
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOEquals) {
      JDOEquals other = (JDOEquals)obj;
      return (left.equals(other.left) &&
              right.equals(other.right));
    }
    return false;
  }

  public String toString() {
    return left + " = " + right;
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(left);
    visitor.visitable(right);
  }
  
}






