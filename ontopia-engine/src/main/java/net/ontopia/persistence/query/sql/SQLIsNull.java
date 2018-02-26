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
 * INTERNAL: SQL condition: is null<p>
 *
 * A NULL condition tests for nulls.<p>
 */

public class SQLIsNull implements SQLExpressionIF {

  protected SQLValueIF value;
  
  public SQLIsNull(SQLValueIF value) {
    this.value = value;
  }

  @Override
  public int getType() {
    return IS_NULL;
  }

  public SQLValueIF getValue() {
    return value;
  }

  public void setValue(SQLValueIF value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return getValue() + " is null";
  }
  
}





