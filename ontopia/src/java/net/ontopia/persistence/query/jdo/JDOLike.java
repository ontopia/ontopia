
// $Id: JDOLike.java,v 1.3 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL method: String.like(String). Syntax: 'A.like("value")'
 */

public class JDOLike implements JDOExpressionIF {

  protected JDOValueIF left;
  protected JDOValueIF right;
  protected boolean caseSensitive;

  public JDOLike(JDOValueIF left, JDOValueIF right, boolean caseSensitive) {
    // Can be string, field, parameter, variable of String type
    // FIXME: Prevent other types to be set
    this.left = left;
    this.right = right;
    this.caseSensitive = caseSensitive;
  }
  
  public int getType() {
    return LIKE;
  }
  
  public JDOValueIF getLeft() {
    return left;
  }
  
  public JDOValueIF getRight() {
    return right;
  }

  public boolean getCaseSensitive() {
    return caseSensitive;
  }
  
  public int hashCode() {
    return left.hashCode() + right.hashCode() + STARTS_WITH;
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOLike) {
      JDOLike other = (JDOLike)obj;
      return (left.equals(other.left) &&
              right.equals(other.right));
    }
    return false;
  }

  public String toString() {
    return left + ".like(" + right + ")";
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(left);
    visitor.visitable(right);
  }
  
}
