
package net.ontopia.topicmaps.nav2.core;

/**
 * INTERNAL: Exception for a problem occurring when using the Navigator
 * Tag Library.  Used when the given object type does not match,
 * unknown functions are called, etc.
 *
 * <p><b>Note:</b> That this is not a runtime exception in the sense
 * that this class is inherited from java.lang.RuntimeException and
 * therefore this exception has to be caught explicitly.</p>
 */
public class NavigatorRuntimeException extends NavigatorTagException {

  /**
   * INTERNAL: Constructor with empty error message.
   */
  public NavigatorRuntimeException() {
    super();
  }
  
  /**
   * INTERNAL: constructor with a message.
   */
  public NavigatorRuntimeException(String message) {
    super(message);
  }
  
  /**
   * INTERNAL: Wraps another exception.
   * @since 1.3.4
   */
  public NavigatorRuntimeException(Throwable cause) {
    super(cause);
  }
  
  /**
   * INTERNAL: Wraps another exception, and adds a message.
   * @since 1.3.4
   */
  public NavigatorRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
