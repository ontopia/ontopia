
package net.ontopia.persistence.query.sql;

/**
 * INTERNAL: SQL condition: in. Evaluates to true if the left
 * value contains the right value.<p>
 *
 * An IN condition tests for existence of rows in a subquery.<p>
 */

public class SQLIn implements SQLExpressionIF {

  protected SQLValueIF left;
  protected SQLValueIF right;

  public SQLIn(SQLColumns left, SQLParameter right) {
    this((SQLValueIF)left, (SQLValueIF)right);
  }

  public SQLIn(SQLTuple left, SQLParameter right) {
    this((SQLValueIF)left, (SQLValueIF)right);
  }
  
  protected SQLIn(SQLValueIF left, SQLValueIF right) {
    if (left == null)
      throw new NullPointerException("Left value must not be null.");
    if (right == null)
      throw new NullPointerException("Right value must not be null.");
    
    this.left = left;
    this.right = right;
  }

  public int getType() {
    return IN;
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
    return getLeft() + " in (" + getRight() + ")";
  }
  
}





