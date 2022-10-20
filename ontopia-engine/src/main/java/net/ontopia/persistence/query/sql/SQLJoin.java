/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
    if (left.getArity() != right.getArity()) {
      throw new IllegalArgumentException("Arities of values are not identical: " +
                                         left + " (arity " + left.getArity() +") " +
                                         right + " (arity " + right.getArity() +")");
    }
    this.left = left;
    this.right = right;
    this.jointype = jointype;
  }

  @Override
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

  @Override
  public int hashCode() {
    return left.hashCode() + right.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SQLJoin) {
      SQLJoin other = (SQLJoin)obj;
      if (left.equals(other.getLeft())) {
        if (right.equals(other.getRight())) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("join:");
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
    return sb.append('(')
        .append(left)
        .append(", ")
        .append(right)
        .append(')')
        .toString();    
  }
  
}





