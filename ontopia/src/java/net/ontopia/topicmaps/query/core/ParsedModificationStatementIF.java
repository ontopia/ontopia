
package net.ontopia.topicmaps.query.core;

import java.util.Map;

/**
 * PUBLIC: Represents a parsed modification statement, such as DELETE,
 * INSERT, UPDATE, and QUERY.
 *
 * @since %NEXT%
 */
public interface ParsedModificationStatementIF extends ParsedStatementIF {

  /**
   * PUBLIC: Runs the statement, returning the number of rows modified.
   */
  public int update() throws InvalidQueryException;

  /**
   * PUBLIC: Runs the statement with the given parameters, returning
   * the number of rows modified.
   */
  public int update(Map<String, ?> params) throws InvalidQueryException;
  
}
