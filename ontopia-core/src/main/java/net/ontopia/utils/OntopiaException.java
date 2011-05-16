// $Id: OntopiaException.java,v 1.8 2008/05/29 10:55:00 geir.gronmo Exp $

package net.ontopia.utils;

import java.io.*;

/**
 * PUBLIC: An exception class that can be used to wrap other
 * exceptions with. This is the most generic exception used.</p>
 */

public class OntopiaException extends Exception {

  protected Throwable cause = null;

  public OntopiaException(String message) {
    super(message);
  }
  
  public OntopiaException(Throwable cause) {
    super();
    this.cause = cause;
  }

  /**
   * @since 1.3
   */
  public OntopiaException(String message, Throwable cause) {
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




