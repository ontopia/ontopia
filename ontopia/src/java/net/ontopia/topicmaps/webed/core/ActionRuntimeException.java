
// $Id: ActionRuntimeException.java,v 1.9 2006/01/05 09:27:09 larsga Exp $

package net.ontopia.topicmaps.webed.core;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: An exception that is thrown when something goes wrong while
 * an action is being executed. Causes form processing to stop and
 * the current transaction to be rolled back.
 */
public class ActionRuntimeException extends OntopiaRuntimeException {
  protected boolean isCritical = true;

  /**
   * PUBLIC: Creates an exception wrapping the Throwable.
   */
  public ActionRuntimeException(Throwable e) {
    super(e);
  }

  /**
   * PUBLIC: Creates an exception with a string message.
   */
  public ActionRuntimeException(String message) {
    super(message);
  }

  /**
   * PUBLIC: Creates an exception with a string message which wraps
   * the Throwable. The messages and stack traces of both exceptions
   * will be shown.
   */
  public ActionRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * PUBLIC: Creates an exception wrapping the Throwable.
   * @param isCritical Flag indicating whether this is a critical error.
   * @since 3.0
   */
  public ActionRuntimeException(Throwable e, boolean isCritial) {
    super(e);
    this.isCritical = isCritial;
  }

  /**
   * PUBLIC: Creates an exception with a string message.
   * @param isCritical Flag indicating whether this is a critical error.
   * @since 3.0
   */
  public ActionRuntimeException(String message, boolean isCritial) {
    super(message);
    this.isCritical = isCritial;
  }

  /**
   * PUBLIC: Creates an exception with a string message which wraps
   * the Throwable. The messages and stack traces of both exceptions
   * will be shown.
   * @param isCritical Flag indicating whether this is a critical error.
   * @since 3.0
   */
  public ActionRuntimeException(String message, Throwable cause,
                                boolean isCritial) {
    super(message, cause);
    this.isCritical = isCritial;
  }

  /**
   * PUBLIC: Returns the value of the critical property.
   * @since 3.0
   */
  public boolean getCritical() {
    return isCritical;
  }
  
  /**
   * PUBLIC: Sets the value of the critical property.
   * @since 3.0
   */
  public void setCritical(boolean isCritical) {
    this.isCritical = isCritical;
  }
  
}
