package ontopoly.utils;

import net.ontopia.utils.OntopiaRuntimeException;

public class NoSuchTopicException extends OntopiaRuntimeException {

  private static final long serialVersionUID = 1L;

  public NoSuchTopicException(String message) {
    super(message);
  }

  public NoSuchTopicException(Throwable cause) {
    super(cause);
  }

  public NoSuchTopicException(String message, Throwable cause) {
    super(message, cause);
  }

}
