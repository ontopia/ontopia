
package net.ontopia.topicmaps.query.core;

/**
 * PUBLIC: This exception is used to indicate that there is something
 * wrong with the query, whether syntactically or semantically.
 */
public class InvalidQueryException extends QueryException {
  public InvalidQueryException(String msg) {
    super(msg);
  }
  public InvalidQueryException(Throwable cause) {
    super(cause);
  }
  public InvalidQueryException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
