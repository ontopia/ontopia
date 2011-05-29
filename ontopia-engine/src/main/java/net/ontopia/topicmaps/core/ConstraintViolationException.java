
package net.ontopia.topicmaps.core;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: Thrown when an object model constraint is violated.</p>
 *
 * Extends OntopiaRuntimeException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 */
public class ConstraintViolationException extends OntopiaRuntimeException {

  public ConstraintViolationException(Throwable cause) {
    super(cause);
  }

  public ConstraintViolationException(String message) {
    super(message);
  }

  public ConstraintViolationException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
