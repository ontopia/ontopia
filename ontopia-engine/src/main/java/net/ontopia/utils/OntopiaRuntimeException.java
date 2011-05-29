
package net.ontopia.utils;

import java.io.*;

/**
 * PUBLIC: A runtime exception class that can be used to wrap other
 * exceptions with. This is the most generic runtime exception
 * used.</p>
 */

public class OntopiaRuntimeException extends RuntimeException {

  private static final long serialVersionUID = -9053271209193824545L;
  
  protected Throwable cause = null;
  
  public OntopiaRuntimeException(String message) {
    super(message);
  }
  
  public OntopiaRuntimeException(Throwable cause) {
    super();
    this.cause = cause;
  }
  
  /**
   * @since 1.3
   */
  public OntopiaRuntimeException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }

  /**
   * @since 1.3
   */
  public Throwable getCause() {
    return cause;
  }

  /**
   * @since 1.3.2
   */
  public String getMessage() {
    String message = super.getMessage();
    if (message != null)
      return message;
    else
      return cause.toString();
  }
  
  public void printStackTrace() {
    super.printStackTrace();
    if (cause != null) {
      System.err.println("Caused by:");
      cause.printStackTrace();
    }
  }
  public void printStackTrace(PrintStream ps) {
    super.printStackTrace(ps);
    if (cause != null) {
      ps.println("Caused by:");
      cause.printStackTrace(ps);
    }
  }
  public void printStackTrace(PrintWriter pw) {
    super.printStackTrace(pw);
    if (cause != null) {
      pw.println("Caused by:");
      cause.printStackTrace(pw);
    }
  }
  
}
