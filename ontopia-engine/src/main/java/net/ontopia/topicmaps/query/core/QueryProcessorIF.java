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

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * PUBLIC: This is the interface that must be implemented by tolog
 * query processors. It is used by client applications to execute
 * queries.
 */
public interface QueryProcessorIF {

  /**
   * PUBLIC: Parses and executes the query, returning the results.
   */
  public QueryResultIF execute(String query)
    throws InvalidQueryException;

  /**
   * PUBLIC: Parses and executes the query in the given context,
   * returning the results.
   *
   * @since 2.1
   */
  public QueryResultIF execute(String query, DeclarationContextIF context)
    throws InvalidQueryException;

  /**
   * PUBLIC: Parses and executes the query binding the parameters in
   * the query to the values given in the 'arguments' map, returning
   * the results.
   *
   * @since 2.0
   */
  public QueryResultIF execute(String query, Map<String, ?> arguments)
    throws InvalidQueryException;

  /**
   * PUBLIC: Parses and executes the query in the given context
   * binding the parameters in the query to the values given in the
   * 'arguments' map, returning the results.
   *
   * @since 2.1
   */
  public QueryResultIF execute(String query, Map<String, ?> arguments,
                               DeclarationContextIF context)
    throws InvalidQueryException;

  /**
   * PUBLIC: Runs the update statement, returning the number of
   * modified objects.
   * @since 5.1.0
   */
  public int update(String query) throws InvalidQueryException;

  /**
   * PUBLIC: Runs the update statement in the given declaration
   * context, returning the number of modified objects.
   * @since 5.1.0
   */
  public int update(String query, DeclarationContextIF context)
    throws InvalidQueryException;
  
  /**
   * PUBLIC: Runs the update statement with the given parameters,
   * returning the number of modified objects.
   * @since 5.1.0
   */
  public int update(String query, Map<String, ?> arguments)
    throws InvalidQueryException;

  /**
   * PUBLIC: Runs the update statement in the given declaration
   * context with the given parameters, returning the number of
   * modified objects.
   * @since 5.1.0
   */
  public int update(String query, Map<String, ?> arguments,
                    DeclarationContextIF context)
    throws InvalidQueryException;
  
  /**     
   * PUBLIC: Parses the query, returning an object representing the
   * result.
   */
  public ParsedQueryIF parse(String query)
    throws InvalidQueryException;

  /**     
   * PUBLIC: Parses the query in the given context, returning an
   * object representing the result.
   *
   * @since 2.1
   */
  public ParsedQueryIF parse(String query, DeclarationContextIF context)
    throws InvalidQueryException;

  /**     
   * PUBLIC: Parses the update statement, returning an object
   * representing the result.
   * @since 5.1.0
   */
  public ParsedModificationStatementIF parseUpdate(String statement)
    throws InvalidQueryException;

  /**     
   * PUBLIC: Parses the update statement in the given context,
   * returning an object representing the result.
   * @since 5.1.0
   */
  public ParsedModificationStatementIF parseUpdate(String statement,
                                                   DeclarationContextIF context)
    throws InvalidQueryException;
 
  /**
   * DEPRECATED: Loads a set of rules into the query processor from a
   * string. The rules will then be available for use in queries
   * throughout the lifetime of the current scope.
   *
   * @deprecated use rule import declaration instead, or contexts
   */
  public void load(String ruleset)
    throws InvalidQueryException;

  /**     
   * DEPRECATED: Loads a set of rules into the query processor from a
   * reader object. The rules will then be available for use in
   * queries throughout the lifetime of the current scope.
   *
   * @since 1.4
   * @deprecated use rule import declaration instead, or contexts
   */
  public void load(Reader ruleset)
    throws InvalidQueryException, IOException;
}
