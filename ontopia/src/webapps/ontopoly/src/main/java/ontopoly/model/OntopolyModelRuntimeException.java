package ontopoly.model;

import net.ontopia.utils.OntopiaRuntimeException;

public class OntopolyModelRuntimeException extends OntopiaRuntimeException {

  private static final long serialVersionUID = 1L;

  public OntopolyModelRuntimeException(String message) {
    super(message);
  }

  public OntopolyModelRuntimeException(Throwable cause) {
    super(cause);
  }

  public OntopolyModelRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

}
