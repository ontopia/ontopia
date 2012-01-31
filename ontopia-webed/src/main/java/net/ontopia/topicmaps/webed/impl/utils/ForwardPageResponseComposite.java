
package net.ontopia.topicmaps.webed.impl.utils;

import java.io.Serializable;

import net.ontopia.topicmaps.webed.impl.basic.ActionForwardPageIF;

/**
 * INTERNAL: Container to store an action forward page object together
 * with a action response type, which enables to store the forward
 * information as a triple consisting of (key: forward id, value =
 * forward page + response type).
 *
 * @see net.ontopia.topicmaps.webed.impl.utils.ActionConfigContentHandler
 */
public class ForwardPageResponseComposite implements Serializable {

  protected ActionForwardPageIF forwardPage;
  protected Integer responseType;
  
  public ForwardPageResponseComposite(ActionForwardPageIF forwardPage,
                                      int responseType) {
    this.forwardPage = forwardPage;
    this.responseType = new Integer(responseType);
  }

  public ActionForwardPageIF getForwardPage() {
    return forwardPage;
  }

  public void setForwardPage(ActionForwardPageIF forwardPage) {
    this.forwardPage = forwardPage;
  }

  public int getResponseType() {
    return responseType.intValue();
  }

  public void setResponseType(int responseType) {
    this.responseType = new Integer(responseType);
  }

  // --- overwrite methods from Object implementation

  public int hashCode() {
    return forwardPage.hashCode() + responseType.hashCode();
  }

  public boolean equals(Object obj) {
    if (obj instanceof ForwardPageResponseComposite) {
      ForwardPageResponseComposite comp = (ForwardPageResponseComposite) obj;
      return (comp.getForwardPage().equals(forwardPage)
              && (comp.getResponseType() == responseType.intValue()));
    } else
      return false;
  }
  
}
