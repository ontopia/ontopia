
// $Id: NavigatorTagException.java,v 1.7 2007/07/13 12:35:07 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.core;

import java.io.*;
import javax.servlet.jsp.JspTagException;

/**
 * INTERNAL: base class for a generic problem occurring when
 * using the Navigator Tag Library. 
 * Please use a more appropiate, specific subclass.
 *
 * @see net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException
 * @see net.ontopia.topicmaps.nav2.core.NavigatorUserException
 * @see net.ontopia.topicmaps.nav2.core.NavigatorCompileException
 */
public class NavigatorTagException extends JspTagException {

  protected Throwable cause = null;

  /**
   * INTERNAL: constructor with empty error message.
   */
  public NavigatorTagException() {
    super();
  }
  
  /**
   * INTERNAL: constructor with a message.
   */
  public NavigatorTagException(String message) {
    super(message);
  }
  
  /**
   * @since 1.3.4
   */
  public NavigatorTagException(Throwable cause) {
    super();
    this.cause = cause;
  }
  
  /**
   * @since 1.3.4
   */
  public NavigatorTagException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }

  /**
   * @since 1.3.4
   */
  public Throwable getCause() {
    return cause;
  }
  
  public String getMessage() {
    if (cause == null)
      return super.getMessage();
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
