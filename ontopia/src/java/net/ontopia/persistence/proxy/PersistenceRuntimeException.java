
// $Id: PersistenceRuntimeException.java,v 1.5 2004/07/02 15:01:53 grove Exp $

package net.ontopia.persistence.proxy;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Thrown when persistence related problems occur.</p>
 */

public class PersistenceRuntimeException extends OntopiaRuntimeException {

  public PersistenceRuntimeException(Throwable e) {
    super(e);
  }

  public PersistenceRuntimeException(String message) {
    super(message);
  }

  public PersistenceRuntimeException(String message, Throwable e) {
    super(message, e);
  }
  
}
