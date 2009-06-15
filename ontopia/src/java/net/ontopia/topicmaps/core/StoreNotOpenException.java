
// $Id: StoreNotOpenException.java,v 1.5 2004/11/30 11:47:32 grove Exp $

package net.ontopia.topicmaps.core;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: An exception that is thrown when parts of a store is
 * accessed without the store being open.</p>
 *
 * Extends OntopiaRuntimeException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 */

public class StoreNotOpenException extends OntopiaRuntimeException {

  public StoreNotOpenException(Throwable cause) {
    super(cause);
  }

  public StoreNotOpenException(String message) {
    super(message);
  }

  public StoreNotOpenException(String message, Throwable cause) {
    super(message, cause);
  }
  
}





