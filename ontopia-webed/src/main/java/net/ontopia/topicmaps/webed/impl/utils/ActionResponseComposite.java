
package net.ontopia.topicmaps.webed.impl.utils;

import java.io.Serializable;

import net.ontopia.topicmaps.webed.impl.basic.ActionInGroup;

/**
 * INTERNAL: Container to store an action object together with a
 * action response type, which enables to store the forward
 * information as a triple consisting of (key: ActionResponseComposite
 * = action + response type, value: ActionForwardPageIF).
 *
 * @see net.ontopia.topicmaps.webed.impl.basic.ActionGroup
 */
public class ActionResponseComposite implements Serializable {

  protected ActionInGroup action;
  protected Integer responseType;
  
  public ActionResponseComposite(ActionInGroup action, int responseType) {
    this.action = action;
    this.responseType = new Integer(responseType);
  }

  public ActionInGroup getAction() {
    return action;
  }

  public void setAction(ActionInGroup action) {
    this.action = action;
  }

  public int getResponseType() {
    return responseType.intValue();
  }

  public void setResponseType(int responseType) {
    this.responseType = new Integer(responseType);
  }

  // --- overwrite methods from Object implementation

  public int hashCode() {
    return action.hashCode() + responseType.hashCode();
  }

  public boolean equals(Object obj) {
    if (obj instanceof ActionResponseComposite) {
      ActionResponseComposite comp = (ActionResponseComposite) obj;
      return (comp.getAction().equals(action)
              && (comp.getResponseType() == responseType.intValue()));
    } else
      return false;
  }
  
}
