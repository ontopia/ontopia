package net.ontopia.tropics.exceptions;

@SuppressWarnings("serial")
public class UnknownParameterException extends RuntimeException {

  public UnknownParameterException() {
    super();
  }

  public UnknownParameterException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnknownParameterException(String message) {
    super(message);
  }

  public UnknownParameterException(Throwable cause) {
    super(cause);
  }

}
