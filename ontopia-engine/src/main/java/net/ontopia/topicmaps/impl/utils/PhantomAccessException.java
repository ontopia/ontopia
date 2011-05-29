
package net.ontopia.topicmaps.impl.utils;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Thrown when a phantom object is being accessed in a way
 * that is not allowed.</p>
 */

public class PhantomAccessException extends OntopiaRuntimeException {

  public PhantomAccessException(String message) {
    super(message);
  }

}





