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

import java.util.Objects;

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
    Objects.requireNonNull(left, "Left value must not be null.");
    Objects.requireNonNull(right, "Right value must not be null.");
    
    this.left = left;
    this.right = right;
  }

  @Override
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

  @Override
  public String toString() {
    return getLeft() + " in (" + getRight() + ")";
  }
  
}





