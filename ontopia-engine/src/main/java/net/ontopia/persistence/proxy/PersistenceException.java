
// $Id: PersistenceException.java,v 1.6 2003/10/01 14:05:48 grove Exp $

package net.ontopia.persistence.proxy;

import net.ontopia.utils.OntopiaException;

/**
 * INTERNAL: Thrown when persistence related problems occur.</p>
 */

public class PersistenceException extends OntopiaException {

  public PersistenceException(Throwable cause) {
    super(cause);
  }

  public PersistenceException(String message) {
    super(message);
  }

  public PersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
