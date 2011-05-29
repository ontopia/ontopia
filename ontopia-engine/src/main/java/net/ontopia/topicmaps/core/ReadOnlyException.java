
package net.ontopia.topicmaps.core;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: Thrown when changes are attempted made on read-only objects.</p>
 */
public class ReadOnlyException extends OntopiaRuntimeException {

  public ReadOnlyException() {
    this("Read-only objects cannot be modified.");
  }

  public ReadOnlyException(Throwable cause) {
    super(cause);
  }

  public ReadOnlyException(String message) {
    super(message);
  }

  public ReadOnlyException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
