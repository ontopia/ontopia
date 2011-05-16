
// $Id: PhantomAccessException.java,v 1.1 2004/12/03 11:37:10 grove Exp $

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





