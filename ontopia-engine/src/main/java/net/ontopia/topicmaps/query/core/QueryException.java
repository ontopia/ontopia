
package net.ontopia.topicmaps.query.core;

import net.ontopia.utils.OntopiaException;

/**
 * PUBLIC: Common super-exception for all exceptions thrown by the
 * query engine.
 */
public class QueryException extends OntopiaException {
  public QueryException(String msg) {
    super(msg);
  }
  public QueryException(Throwable cause) {
    super(cause);
  }
  public QueryException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
