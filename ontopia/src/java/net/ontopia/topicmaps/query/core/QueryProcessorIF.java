
// $Id: QueryProcessorIF.java,v 1.18 2005/07/13 08:58:29 grove Exp $

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
   */
  public int update(String query) throws InvalidQueryException;
  
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
