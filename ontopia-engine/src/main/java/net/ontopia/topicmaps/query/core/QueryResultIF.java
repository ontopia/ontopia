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

package net.ontopia.topicmaps.query.core;

/**
 * PUBLIC: Used to represent the results of queries. A query result is
 * conceptually a table where each column corresponds to a variable
 * bound by the query, and each row corresponds to a match to the
 * query. That is, each row has a value for each column, and this is
 * the value of the variable in that particular match.</p>
 *
 * <p>The query result object always has a current row in the result
 * (except before the first call to <tt>next()</tt>), and the
 * <tt>getValue(int ix)</tt> method will return the value in column
 * <tt>ix</tt> of this row. To find the column number for a particular
 * variable, call <tt>getIndex("VAR")</tt>; alternatively, call
 * <tt>getValue("VAR")</tt> directly.</p>
 *
 * <p>The <tt>next()</tt> method is used to simultaneously step to the
 * next row <em>and</em> check if there is a next row.
 */
public interface QueryResultIF extends AutoCloseable {

  /**
   * PUBLIC: Steps to the next match, returning true if a valid match
   * was found, and false if there are no more matches. Must be called
   * before values can be returned.
   */
  boolean next();

  /**
   * PUBLIC: Returns the number of columns in the result.
   */
  int getWidth();

  /**
   * PUBLIC: Returns the index of the named column. Returns -1 if the
   * column does not exist. The column index is zero-based.
   */
  int getIndex(String colname);

  /**
   * PUBLIC: Returns the names of the columns.
   */
  String[] getColumnNames();

  /**
   * PUBLIC: Returns the name of the given column.  The column index
   * is zero-based.
   *
   * @throws IndexOutOfBoundsException if there is no such column.
   */
  String getColumnName(int ix);

  /**
   * PUBLIC: Returns the value in the given column in the current
   * match. The column index is zero-based. Requires
   * <code>next()</code> to have been called first.
   *
   * @throws IndexOutOfBoundsException if there is no such column.
   */
  Object getValue(int ix);
  
  /**
   * PUBLIC: Returns the value in the given column in the current
   * match.  Requires <code>next()</code> to have been called first.
   * @throws IllegalArgumentException if there is no such column.
   */
  Object getValue(String colname);

  /**
   * PUBLIC: Returns the current match as an array of values. Note
   * that the returned array should not be modified as it may lead to
   * undefined results. Requires <code>next()</code> to have been
   * called first.
   */
  Object[] getValues();

  /**
   * PUBLIC: Reads the values of the current match into the specified
   * array. Requires <code>next()</code> to have been called first.
   *
   * @since 1.3.2
   */
  Object[] getValues(Object[] values);

  /**
   * PUBLIC: Closes the query result, which allows it to free its
   * resources.<p>
   *
   * @since 1.3.4
   */
  void close();
  
}
