// $Id: SQLJoin.java,v 1.15 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.sql;


/**
 * INTERNAL: SQL expression: join. Represents a join between two
 * tables. A join is a query that combines rows from two or more
 * tables, views, or materialized views ("snapshots").<p>
 *
 * Cross joins, left outer joins and right outer joins are
 * supported.<p>
 */

public class SQLJoin implements SQLExpressionIF {

  // Notes:
  //
  // - left and right should not be the same.
  
  public static final int CROSS = 1; // Default
  public static final int LEFT_OUTER = 2;
  public static final int RIGHT_OUTER = 3;

  protected int jointype = CROSS;

  protected SQLColumns left;
  protected SQLColumns right;
    
  public SQLJoin() {
  }

  public SQLJoin(SQLColumns left, SQLColumns right) {
    this(left, right, CROSS);
  }
  
  public SQLJoin(SQLColumns left, SQLColumns right, int jointype) {
    // Complain if arities are different
    if (left.getArity() != right.getArity())
      throw new IllegalArgumentException("Arities of values are not identical: " +
                                         left + " (arity " + left.getArity() +") " +
                                         right + " (arity " + right.getArity() +")");
    this.left = left;
    this.right = right;
    this.jointype = jointype;
  }

  public int getType() {
    return JOIN;
  }

  public int getJoinType() {
    // CROSS join is the default.
    // if (jointype == null) return CROSS;
    return jointype;
  }

  public void setJoinType(int jointype) {
    this.jointype = jointype;
  }
  
  public SQLColumns getLeft() {
    return left;
  }
  
  public void setLeft(SQLColumns left) {
    this.left = left;
  }

  public SQLColumns getRight() {
    return right;
  }

  public void setRight(SQLColumns right) {
    this.right = right;
  }

  public int hashCode() {
    return left.hashCode() + right.hashCode();
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof SQLJoin) {
      SQLJoin other = (SQLJoin)obj;
      if (left.equals(other.getLeft()))
        if (right.equals(other.getRight()))
          return true;
    }
    return false;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("join:");
    switch (getJoinType()) {
    case CROSS:
      sb.append("cross");
      break;
    case LEFT_OUTER:
      sb.append("left-outer");
      break;
    case RIGHT_OUTER:
      sb.append("right-outer");
      break;
    default:
      sb.append("unknown");
    }
    sb.append("(");
    sb.append(left);
    sb.append(", ");
    sb.append(right);
    sb.append(")");
    return sb.toString();    
  }
  
}





