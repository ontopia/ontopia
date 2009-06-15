// $Id: NavigatorUserException.java,v 1.6 2007/07/13 12:35:07 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.core;

/**
 * INTERNAL: Exception for a problem occurring when
 * using the Navigator Tag Library.
 * Thrown by the error tag.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.ErrorTag
 */
public class NavigatorUserException extends NavigatorTagException {

  /**
   * INTERNAL: constructor with empty error message.
   */
  public NavigatorUserException() {
    super();
  }
  
  /**
   * INTERNAL: constructor with a message.
   */
  public NavigatorUserException(String message) {
    super(message);
  }
  
  /**
   * @since 1.3.4
   */
  public NavigatorUserException(Throwable cause) {
    super(cause);
  }
  
  /**
   * @since 1.3.4
   */
  public NavigatorUserException(String message, Throwable cause) {
    super(message, cause);
  }
  
}





