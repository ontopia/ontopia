
// $Id: SQLLike.java,v 1.10 2004/03/25 14:20:35 larsga Exp $

package net.ontopia.persistence.query.sql;

/** 
 * INTERNAL: SQL condition: like<p>
 *
 * A LIKE condition specifies a test involving pattern matching. Note
 * that right value must be a pattern, i.e. a string.<p>
 */

public class SQLLike implements SQLExpressionIF {

  protected SQLValueIF left;
  protected SQLValueIF right;
  protected boolean caseSensitive;

  public SQLLike(SQLValueIF left, SQLValueIF right, boolean caseSensitive) {
    // Complain if arities are different
    if (left.getArity() != right.getArity())
      throw new IllegalArgumentException("Arities of values are not identical: " +
                                         left + " (arity " + left.getArity() +") " +
                                         right + " (arity " + right.getArity() +")");
    this.left = left;
    this.right = right;
    this.caseSensitive = caseSensitive;
  }

  public int getType() {
    return LIKE;
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

  public boolean getCaseSensitive() {
    return caseSensitive;
  }

  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public String toString() {
    return getLeft() + " like " + getRight();
  }
  
}
