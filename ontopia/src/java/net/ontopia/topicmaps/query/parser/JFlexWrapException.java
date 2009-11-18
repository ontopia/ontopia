
package net.ontopia.topicmaps.query.parser;

/**
 * INTERNAL: Exception used to wrap other exceptions so that they can
 * be thrown from inside JFlex-generated code.
 */
public class JFlexWrapException extends RuntimeException {
  public Exception exception;

  public JFlexWrapException(Exception exception) {
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
