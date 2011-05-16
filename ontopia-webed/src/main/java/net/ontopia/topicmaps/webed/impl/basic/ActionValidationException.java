
// $Id: ActionValidationException.java,v 1.1 2005/09/14 07:20:17 grove Exp $

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
