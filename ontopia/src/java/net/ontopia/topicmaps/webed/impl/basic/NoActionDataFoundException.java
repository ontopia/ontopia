
// $Id: NoActionDataFoundException.java,v 1.2 2005/09/07 07:49:51 grove Exp $

package net.ontopia.topicmaps.webed.impl.basic;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Thrown by when no action data is found for the given request id.
 */
public class NoActionDataFoundException extends OntopiaRuntimeException {
  
  protected Object userObject;
  
  public NoActionDataFoundException(String msg) {
    super(msg);
  }
 
  public Object getUserObject() {
    return userObject;
  }
  
  public void setUserObject(Object userObject) {
    this.userObject = userObject;
  }
  
}
