
// $Id: BadObjectReferenceException.java,v 1.1 2007/03/19 13:58:57 grove Exp $

package net.ontopia.topicmaps.query.core;

/**
 * PUBLIC: This exception is used to indicate that there is something
 * wrong with the query, whether syntactically or semantically.
 */
public class BadObjectReferenceException extends InvalidQueryException {
  public BadObjectReferenceException(String msg) {
    super(msg);
  }
  public BadObjectReferenceException(Throwable cause) {
    super(cause);
  }
  public BadObjectReferenceException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
