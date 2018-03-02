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
 * INTERNAL: Represents an aggregate function in a SQL query.
 */

public interface SQLAggregateIF {
  
  /**
   * INTERNAL: Constant referring to the COUNT aggregate function.
   */
  int COUNT = 1;
  
  /**
   * INTERNAL: Returns the aggregate function type indicated by one of
   * the constants in the {@link SQLAggregateIF} interface.
   */
  int getType();

  /**
   * INTERNAL: Returns the SQLValueIF that the aggregate function is
   * to be evaluated against.
   */
  SQLValueIF getValue();

  /**
   * INTERNAL: Sets the SQLValueIF that the aggregate function is
   * to be evaluated against.
   */
  void setValue(SQLValueIF value);

  /**
   * INTERNAL: The <i>column</i> alias to use if this value is
   * included in the projection. The SQL select syntax is typically
   * like "select value as <calias> from foo".
   */
  String getAlias();

  /**
   * INTERNAL: Sets the column alias.
   */
  void setAlias(String alias);

  /**
   * INTERNAL: Returns true if this aggregate is a reference to
   * another.
   */
  boolean isReference();

  /**
   * INTERNAL: Returns the referenced aggregate if one exists.
   */
  SQLAggregateIF getReference();
  
}
