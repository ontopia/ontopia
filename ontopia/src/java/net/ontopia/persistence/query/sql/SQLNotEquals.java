// $Id: SQLNotEquals.java,v 1.3 2004/05/21 12:29:18 grove Exp $

package net.ontopia.persistence.query.sql;

/**
 * INTERNAL: SQL condition: not equals (!=)
 */

public class SQLNotEquals implements SQLExpressionIF {

  protected SQLValueIF left;
  protected SQLValueIF right;

  public SQLNotEquals(SQLValueIF left, SQLValueIF right) {
    // Complain if arities are different
    if (left.getArity() != right.getArity())
      throw new IllegalArgumentException("Arities of values are not identical: " +
                                         left + " (arity " + left.getArity() +") " +
                                         right + " (arity " + right.getArity() +")");
    this.left = left;
    this.right = right;
  }

  public int getType() {
    return NOT_EQUALS;
  }

  public SQLValueIF getLeft() {
    return left;
  }

  public void setLeft(SQLValueIF left) {
    this.left = left;
  }

  public SQLValueIF getRight() {
    return right;
  }

  public void setRight(SQLValueIF right) {
    this.right = right;
  }

  public String toString() {
    return getLeft() + " != " + getRight();
  }
  
}





