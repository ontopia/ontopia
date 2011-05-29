
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL method: Collection.contains(Object). Syntax: 'A.contains(B)'.
 */

public class JDOContains implements JDOExpressionIF {

  protected JDOValueIF left;
  protected JDOValueIF right;

  public JDOContains(JDOField left, JDOValueIF right) {
    this((JDOValueIF)left, right);
  }
  
  public JDOContains(JDOParameter left, JDOVariable right) {
    this((JDOValueIF)left, (JDOValueIF)right);
  }

  public JDOContains(JDOCollection left, JDOValueIF right) {
    this((JDOValueIF)left, right);
  }

  public JDOContains(JDOVariable left, JDOValueIF right) {
    this((JDOValueIF)left, right);
  }
  
  protected JDOContains(JDOValueIF left, JDOValueIF right) {
    // FIXME: Is the value always a field?
    this.left = left;
    this.right = right;
  }
  
  public int getType() {
    return CONTAINS;
  }
  
  public JDOValueIF getLeft() {
    return left;
  }
  
  public JDOValueIF getRight() {
    return right;
  }
  
  public int hashCode() {
    return left.hashCode() + right.hashCode() + CONTAINS;
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOContains) {
      JDOContains other = (JDOContains)obj;
      return (left.equals(other.left) &&
              right.equals(other.right));
    }
    return false;
  }

  public String toString() {
    return left + ".contains(" + right + ")";
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(left);
    visitor.visitable(right);
  }
  
}






