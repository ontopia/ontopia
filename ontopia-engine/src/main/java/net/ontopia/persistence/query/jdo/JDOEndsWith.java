
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL method: String.endsWith(String). Syntax: 'A.endsWith("suffix")'
 */

public class JDOEndsWith implements JDOExpressionIF {

  protected JDOValueIF left;
  protected JDOValueIF right;
    
  public JDOEndsWith(JDOValueIF left, JDOValueIF right) {
    // Note: Left and right must be values of String type
    // FIXME: Prevent other types to be used
    this.left = left;
    this.right = right;
  }
  
  public int getType() {
    return ENDS_WITH;
  }
  
  public JDOValueIF getLeft() {
    return left;
  }
  
  public JDOValueIF getRight() {
    return right;
  }
  
  public int hashCode() {
    return left.hashCode() + right.hashCode() + ENDS_WITH;
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOEndsWith) {
      JDOEndsWith other = (JDOEndsWith)obj;
      return (left.equals(other.left) &&
              right.equals(other.right));
    }
    return false;
  }

  public String toString() {
    return left + ".endsWith(" + right + ")";
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(left);
    visitor.visitable(right);
  }
  
}






