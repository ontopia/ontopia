
package net.ontopia.topicmaps.core;

/**
 * PUBLIC: Thrown when a uniqueness constraint is violated.</p>
 *
 * Extends ConstraintViolationException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 */
public class UniquenessViolationException extends ConstraintViolationException {

  public UniquenessViolationException(Throwable cause) {
    super(cause);
  }

  public UniquenessViolationException(String message) {
    super(message);
  }

  public UniquenessViolationException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
