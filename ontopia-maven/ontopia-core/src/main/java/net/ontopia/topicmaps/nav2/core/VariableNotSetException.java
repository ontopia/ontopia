
// $Id: VariableNotSetException.java,v 1.7 2007/07/13 12:35:07 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.core;

/**
 * INTERNAL: Exception for a problem occurring when using the Navigator
 * Tag Library.  Used when referring to undefined variables.
 *
 * @since 1.3
 */
public class VariableNotSetException extends RuntimeException {

  /**
   * INTERNAL: constructor with empty error message.
   */
  public VariableNotSetException() {
    super();
  }
  
  /**
   * INTERNAL: constructor with the name of the variable which is not set.
   */
  public VariableNotSetException(String varName) {
    super("Stopped page processing, because variable " +
          (varName != null ? "'"+varName+"'" : "{unnamed}") +
          " is not set.");
  }
  
}
