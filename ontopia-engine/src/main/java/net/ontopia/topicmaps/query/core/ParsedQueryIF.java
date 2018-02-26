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

import java.util.Map;
import java.util.List;
import java.util.Collection;

/**
 * PUBLIC: Used to represent parsed queries.
 */
public interface ParsedQueryIF extends ParsedStatementIF {

  /**
   * PUBLIC: Returns the variables in the <tt>select</tt> clause of
   * the query, in the order given there. If there is no
   * <tt>select</tt> clause all the variables used in the query are
   * returned, in no particular order.
   * @return An immutable List of String objects.
   */
  List<String> getSelectedVariables();

  /**
   * PUBLIC: Returns the variables that are counted in the
   * <tt>select</tt> clause of the query. They are returned in no
   * particular order. If there are no counted variables, or if there
   * is no <tt>select</tt> clause an empty collection is returned.
   * @return An immutable Collection of String objects.
   */
  Collection<String> getCountedVariables();

  /**
   * PUBLIC: Returns all the variables used in the query, in no
   * particular order.
   * @return An immutable Collection of String objects.
   */
  Collection<String> getAllVariables();

  /**
   * PUBLIC: Returns the variables listed in the <tt>order by</tt>
   * clause in the order they are given there. In order to see which
   * ones are ascending and which descending, use the
   * <tt>isOrderedAscending</tt> method.
   * @return An immutable List of String objects.
   */
  List<String> getOrderBy();

  /**
   * PUBLIC: Returns true if the named variable is to be sorted in
   * ascending order.
   */
  boolean isOrderedAscending(String name);

  /**
   * PUBLIC: Executes the query, returning the query result. Query
   * results are <em>not</em> cached, so results are up to date.
   */
  QueryResultIF execute() throws InvalidQueryException;

  /**
   * PUBLIC: Executes the query binding the parameters in the query to
   * the values given in the 'arguments' map, returning the query
   * result. Query results are <em>not</em> cached, so results are up
   * to date.
   * @since 2.0
   */
  QueryResultIF execute(Map<String, ?> arguments) throws InvalidQueryException;
  
}
