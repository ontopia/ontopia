package net.ontopia.topicmaps.utils.jtm;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Exception used to indicate errors while importing JTM topic maps.
 */
@SuppressWarnings("serial")
public class JTMException extends OntopiaRuntimeException {
  public JTMException(String message) {
    super(message);
  }

  public JTMException(String message, Throwable cause) {
    super(message, cause);
  }
}
