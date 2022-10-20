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
    if (left.getArity() != right.getArity()) {
      throw new IllegalArgumentException("Arities of values are not identical: " +
                                         left + " (arity " + left.getArity() +") " +
                                         right + " (arity " + right.getArity() +")");
    }
    this.left = left;
    this.right = right;
    this.caseSensitive = caseSensitive;
  }

  @Override
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

  @Override
  public String toString() {
    return getLeft() + " like " + getRight();
  }
  
}
