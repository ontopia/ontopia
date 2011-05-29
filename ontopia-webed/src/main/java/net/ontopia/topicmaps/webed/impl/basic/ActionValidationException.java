
package net.ontopia.topicmaps.webed.impl.basic;

import net.ontopia.topicmaps.webed.core.ActionRuntimeException;

/**
 * INTERNAL: Thrown by when there is a validation problem relating to an action.
 */
public class ActionValidationException extends ActionRuntimeException {
  
  public ActionValidationException(String msg, boolean isCritical) {
    super(msg, isCritical);
  }
  
}
