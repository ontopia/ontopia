// $Id: NotRemovableException.java,v 1.5 2008/06/02 10:50:12 geir.gronmo Exp $

package net.ontopia.topicmaps.core;


/**
 * PUBLIC: Thrown when an object is attempted removed, but cannot.</p>
 *
 * Extends ConstraintViolationException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 */

public class NotRemovableException extends ConstraintViolationException {

  public NotRemovableException(Throwable e) {
    super(e);
  }

  public NotRemovableException(String message) {
    super(message);
  }

  public NotRemovableException(String message, Throwable cause) {
    super(message, cause);
  }
  
}





