
package net.ontopia.topicmaps.nav2.core;

/**
 * INTERNAL: Exception for a problem occurring when
 * using the Navigator Tag Library.
 * Used when attributes are not specified, given the
 * wrong values, tags have the wrong parents etc.
 */
public class NavigatorCompileException extends NavigatorTagException {

  /**
   * INTERNAL: constructor with empty error message.
   */
  public NavigatorCompileException() {
    super();
  }
  
  /**
   * INTERNAL: constructor with a message.
   */
  public NavigatorCompileException(String message) {
    super(message);
  }
  
  /**
   * @since 1.3.4
   */
  public NavigatorCompileException(Throwable cause) {
    super(cause);
  }
  
  /**
   * @since 1.3.4
   */
  public NavigatorCompileException(String message, Throwable cause) {
    super(message, cause);
  }
  
}





