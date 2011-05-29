
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL method: String.startsWith(String). Syntax: 'A.startsWith("prefix")'
 */

public class JDOStartsWith implements JDOExpressionIF {

  protected JDOValueIF left;
  protected JDOValueIF right;
    
  public JDOStartsWith(JDOValueIF left, JDOValueIF right) {
    // Can be string, field, parameter, variable of String type
    // FIXME: Prevent other types to be set
    this.left = left;
    this.right = right;
  }
  
  public int getType() {
    return STARTS_WITH;
  }
  
  public JDOValueIF getLeft() {
    return left;
  }
  
  public JDOValueIF getRight() {
    return right;
  }
  
  public int hashCode() {
    return left.hashCode() + right.hashCode() + STARTS_WITH;
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOStartsWith) {
      JDOStartsWith other = (JDOStartsWith)obj;
      return (left.equals(other.left) &&
              right.equals(other.right));
    }
    return false;
  }

  public String toString() {
    return left + ".startsWith(" + right + ")";
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(left);
    visitor.visitable(right);
  }
  
}






