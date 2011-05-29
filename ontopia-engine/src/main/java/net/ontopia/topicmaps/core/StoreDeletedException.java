
package net.ontopia.topicmaps.core;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: An exception that is thrown when a store has been
 * deleted. No further access to the store is possible after this has
 * happened.</p>
 *
 * Extends OntopiaRuntimeException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 *
 * @since 2.1
 */

public class StoreDeletedException extends OntopiaRuntimeException {

  public StoreDeletedException(Throwable cause) {
    super(cause);
  }

  public StoreDeletedException(String message) {
    super(message);
  }

  public StoreDeletedException(String message, Throwable cause) {
    super(message, cause);
  }
  
}





