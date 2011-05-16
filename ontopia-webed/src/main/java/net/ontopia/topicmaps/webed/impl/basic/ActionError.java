package net.ontopia.topicmaps.webed.impl.basic;

import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionData;

public class ActionError {

  protected ActionRuntimeException exception;
  protected ActionData data;
  protected String[] values;
  
  public ActionError(ActionRuntimeException cause, ActionData data) {
    this.exception = cause;
    this.data = data;
  }
  
  public ActionError(ActionRuntimeException cause, ActionData data, String[] values) {
    this.exception = cause;
    this.data = data;
    this.values = values;
  }
 
  public ActionRuntimeException getException() {
    return exception;
  }

}
